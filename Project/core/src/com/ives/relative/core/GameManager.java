package com.ives.relative.core;

import com.artemis.Manager;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.managers.*;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.network.Network;
import com.ives.relative.systems.MovementSystem;
import com.ives.relative.systems.WorldSystem;

/**
 * Created by Ives on 4/12/2014.
 * The core of this game, it is both used by the server and the client.
 * There are two instances of this manager on a local computer, the server instance and the client instance.
 */
public class GameManager extends Manager {
    public boolean isServer;
    public World world;
    public Network network;

    /**
     * This GameManager can be a server AND a client. This way it will be handled
     * as the same in the code. The only difference is the initialization (so the client
     * processes get added to the engine and the server processes as well).
     *
     * @param isServer the boolean which says if the GameManager is an instance of a server or client.
     */
    public GameManager(boolean isServer) {
        this.isServer = isServer;
    }

    /**
     * Register common systems between client and server.
     */
    public void registerSystems() {
        world.setSystem(new WorldSystem());
        world.setSystem(new MovementSystem());
    }

    /**
     * Register command managers between client and server.
     */
    public void registerManagers() {
        world.setManager(new PlanetManager());
        world.setManager(new CollisionManager());
        world.setManager(new TileManager());
        ModuleManager moduleManager = new ModuleManager();
        world.setManager(moduleManager);
        moduleManager.indexModules();
        world.setManager(new CommandManager());
        world.setManager(new GroupManager());
        world.setManager(new NetworkManager());
        world.setManager(new UuidEntityManager());
    }

    public boolean isServer() {
        return isServer;
    }

    /**
     * Common update (gets called each time)
     * This methods passes the update to the engine (common) and the proxy (also not common)
     *
     * @param delta delta time
     */
    public void render(float delta) {
        if (world != null) {
            world.setDelta(delta);
            world.process();
        }
    }
}
