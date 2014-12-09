package com.ives.relative.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.esotericsoftware.kryo.Kryo;
import com.ives.relative.Relative;
import com.ives.relative.assets.modules.Module;
import com.ives.relative.assets.modules.ModuleManager;
import com.ives.relative.core.client.ClientProxy;
import com.ives.relative.core.network.KryoComparator;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.core.server.ServerProxy;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.factories.PlanetFactory;
import com.ives.relative.entities.factories.PlayerFactory;
import com.ives.relative.entities.factories.TileFactory;
import com.ives.relative.entities.systems.MovementSystem;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.planet.TerrainGenerator;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ives on 4/12/2014.
 * The core of this game, it is both used by the server and the client.
 * There are two instances of this manager on a local computer, the server instance and the client instance.
 */
public class GameManager {
    public static PlanetFactory planetFactory;
    public static PlayerFactory playerFactory;
    public static TileFactory tileFactory;
    public static float PHYSICS_ITERATIONS = 1/45f;
    public boolean isServer;
    public Engine engine;
    public TileManager tileManager;
    public Proxy proxy;
    public Relative relative;
    public TerrainGenerator terrainGenerator;
    public ModuleManager moduleManager;
    ArrayList<Class<? extends Object>> kryoList;

    /**
     * This GameManager can be a server AND a client. This way it will be handled
     * as the same in the code. The only difference is the initialization (so the client
     * processes get added to the engine and the server processes as well).
     * @param relative if the main class may be needed, for batch and camera for example
     * @param isServer the boolean which says if the GameManager is an instance of a server or client.
     */
    public GameManager(Relative relative, boolean isServer) {
        this.isServer = isServer;
        this.relative = relative;
        tileManager = new TileManager(this);
        engine = new Engine();

        registerSystems();
        addKryoClasses();

        moduleManager = new ModuleManager(this);
        moduleManager.indexModules();

        planetFactory = new PlanetFactory();
        playerFactory = new PlayerFactory();
        tileFactory = new TileFactory();

        terrainGenerator = new TerrainGenerator(this);

        if (isServer) {
            proxy = new ServerProxy(this);
        } else {
            proxy = new ClientProxy(this, relative.camera, relative.batch);
        }

    }

    public Proxy getProxy() {
        return proxy;
    }

    public void registerSystems() {
        engine.addSystem(new WorldSystem(Family.all(WorldComponent.class, NameComponent.class).get(), GameManager.PHYSICS_ITERATIONS));
        engine.addSystem(new MovementSystem(Family.all(BodyComponent.class).get()));
    }

    /**
     * This class adds al the specified kryo classes from the packages, after this it sorts them and adds them to the
     * arraylist. Another method will register every class to Kryo.
     * I sort every class because the classes may be added in a different order between instances, this is caused by
     * reflection. If the classes are out of order Kryo will not be able to understand the packets sent/received.
     */
    private void addKryoClasses() {
        //Get all the components and add it to the kryo
        Reflections componentReflections = new Reflections("com.ives.relative.entities.components");
        Set<Class<? extends Component>> components = componentReflections.getSubTypesOf(Component.class);

        Reflections packetReflections = new Reflections("com.ives.relative.core.packets");
        Set<Class<? extends Packet>> packets = packetReflections.getSubTypesOf(Packet.class);

        kryoList = new ArrayList<Class<?>>();
        kryoList.addAll(components);
        kryoList.addAll(packets);

        kryoList.sort(new KryoComparator());
    }

    /**
     * The central place for server and client to register the classes used by Kryo.
     * @param kryo the Kryo of the server or the Kryo of the client.
     */
    public void registerKryoClasses(Kryo kryo) {
        kryo.register(SolidTile.class);
        kryo.register(byte[].class);
        kryo.register(NetworkEntity.class);
        kryo.register(ArrayList.class);
        kryo.register(Module.class);

        /*
        kryo.register(BodyComponent.class);
        kryo.register(DurabilityComponent.class);
        kryo.register(HealthComponent.class);
        kryo.register(MovementSpeedComponent.class);
        kryo.register(NameComponent.class);
        kryo.register(TileComponent.class);
        kryo.register(WorldComponent.class);

        kryo.register(NetworkBodyComponent.class);
        kryo.register(NetworkVisualComponent.class);

        kryo.register(ConnectPacket.class);
        kryo.register(CreatePlanetPacket.class);
        kryo.register(DisconnectPacket.class);
        kryo.register(PacketFactory.class);
        kryo.register(PlayerPacket.class);
        kryo.register(PositionPacket.class);
        kryo.register(TilePacket.class);
        kryo.register(ToServerPositionPacket.class);
        kryo.register(RequestTilePacket.class);
        kryo.register(NetworkEntity.class);
        */

        for(Class<?> c : kryoList) {
            kryo.register(c);
        }

        /*
        for(Class component : components) {
            kryo.register(component);
        }

        for(Class packet : packets) {
            kryo.register(packet);
        }
        */
    }

    public boolean isServer() {
        return isServer;
    }

    /**
     * Common update (gets called each time)
     * This methods passes the update to the engine (common) and the proxy (also not common)
     * @param delta delta time
     */
    public void render(float delta) {
        engine.update(delta);
        proxy.update(delta);
    }
}
