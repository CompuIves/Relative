package com.ives.relative.core;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.ives.relative.Relative;
import com.ives.relative.core.client.ClientProxy;
import com.ives.relative.core.packets.EntityPacket;
import com.ives.relative.core.server.ServerProxy;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.planet.tiles.TileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 4/12/2014.
 */
public class GameManager {
    public boolean isServer;

    public Engine engine;
    public TileManager tileManager;

    Proxy proxy;

    public Relative relative;

    public static float PHYSICS_ITERATIONS = 1/45f;

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

        if(isServer)
            proxy = new ServerProxy(this);
        else
            proxy = new ClientProxy(this, relative.camera, relative.batch);

        registerSystems();
    }

    public void registerSystems() {
        engine.addSystem(new WorldSystem(Family.all(WorldComponent.class).get(), GameManager.PHYSICS_ITERATIONS));
    }

    public void registerKryoClasses(Kryo kryo) {
        kryo.register(EntityPacket.class);
    }

    public void render(float delta) {
        engine.update(delta);
        proxy.update(delta);
    }
}
