package com.ives.relative.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.ives.relative.Relative;
import com.ives.relative.core.client.ClientProxy;
import com.ives.relative.core.packets.*;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.core.server.ServerProxy;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.factories.PlayerFactory;
import com.ives.relative.entities.factories.TileFactory;
import com.ives.relative.entities.systems.MovementSystem;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.entities.factories.PlanetFactory;
import com.ives.relative.planet.TerrainGenerator;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Ives on 4/12/2014.
 */
public class GameManager {
    public boolean isServer;

    public Engine engine;
    public TileManager tileManager;

    public Proxy proxy;

    public Relative relative;
    public TerrainGenerator terrainGenerator;

    public static PlanetFactory planetFactory;
    public static PlayerFactory playerFactory;
    public static TileFactory tileFactory;

    public static float PHYSICS_ITERATIONS = 1/45f;

    Set<Class <?extends Packet>> packets;
    Set<Class <?extends Component>> components;

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


        //Get all the components and add it to the kryo
        Reflections componentReflections = new Reflections("com.ives.relative.entities.components");
        components = componentReflections.getSubTypesOf(Component.class);

        Reflections packetReflections = new Reflections("com.ives.relative.core.packets");
        packets = packetReflections.getSubTypesOf(Packet.class);

        planetFactory = new PlanetFactory();
        playerFactory = new PlayerFactory();
        tileFactory = new TileFactory();

        Entity planet = planetFactory.createPlanet("earth", "Earth", new Vector2(0, -10), 8, 3);
        engine.addEntity(planet);
        terrainGenerator = new TerrainGenerator(this);

        if(isServer)
            proxy = new ServerProxy(this);
        else
            proxy = new ClientProxy(this, relative.camera, relative.batch);
    }

    public void registerSystems() {
        engine.addSystem(new WorldSystem(Family.all(WorldComponent.class, NameComponent.class).get(), GameManager.PHYSICS_ITERATIONS));
        engine.addSystem(new MovementSystem(Family.all(BodyComponent.class).get()));
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

        for(Class component : components) {
            kryo.register(component);
        }

        for(Class packet : packets) {
            kryo.register(packet);
        }
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
