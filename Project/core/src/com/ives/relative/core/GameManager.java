package com.ives.relative.core;

import com.artemis.Component;
import com.artemis.World;
import com.esotericsoftware.kryo.Kryo;
import com.ives.relative.Relative;
import com.ives.relative.core.client.ClientProxy;
import com.ives.relative.core.network.KryoComparator;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.core.packets.handshake.modules.notice.StartFileNotice;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.core.server.ServerProxy;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.SolidTile;
import com.ives.relative.managers.TileManager;
import com.ives.relative.managers.assets.modules.Module;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.systems.MovementSystem;
import com.ives.relative.systems.WorldSystem;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ives on 4/12/2014.
 * The core of this game, it is both used by the server and the client.
 * There are two instances of this manager on a local computer, the server instance and the client instance.
 */
public class GameManager {
    public static float PHYSICS_ITERATIONS = 1/45f;
    public boolean isServer;
    public Proxy proxy;
    public Relative relative;

    public ModuleManager moduleManager;
    public World entityWorld;
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
        entityWorld = new World();
        addKryoClasses();

        if (isServer) {
            proxy = new ServerProxy(this, entityWorld);
        } else {
            proxy = new ClientProxy(this, relative.camera, relative.batch, entityWorld);
        }

    }

    public Proxy getProxy() {
        return proxy;
    }

    public void registerCommonSystems(World world) {
        world.setSystem(new WorldSystem(PHYSICS_ITERATIONS));
        world.setSystem(new MovementSystem());
    }

    public void registerCommonManagers(World world) {
        world.setManager(new PlanetManager());
        world.setManager(new TileManager());
        moduleManager = new ModuleManager();
        world.setManager(moduleManager);
        moduleManager.indexModules();
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
        kryo.register(CompleteFileNotice.class);
        kryo.register(StartFileNotice.class);
        kryo.register(FinishFileTransferNotice.class);

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
        entityWorld.setDelta(delta);
        entityWorld.process();
        proxy.update(delta);
    }
}
