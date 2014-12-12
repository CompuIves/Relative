package com.ives.relative.core.server;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {

    public ServerProxy(GameManager game, com.artemis.World entityWorld) {
        ServerProxy.world = entityWorld;
        ServerProxy.game = game;
        Server server = new Server();
        network = new ServerNetwork(game, server, this);
    }

    public void serverAccepted() {
        game.registerCommonManagers(world);
        game.registerCommonSystems(world);
        registerSystems();
        registerManagers();
        world.initialize();
        game.moduleManager.loadModules();
        game.moduleManager.zipAllModules();
        PlanetManager planetManager = game.entityWorld.getManager(PlanetManager.class);
        Entity planet = planetManager.createNewPlanet("earth", "Earth", "ivesiscool", new World(new Vector2(0, -10), true), 8, 10);
        planetManager.generateTerrain(planet);
    }

    public void registerSystems() {

    }

    public void registerManagers() {
        game.entityWorld.setManager(new ServerPlayerManager());
    }

    public void update(float delta) {
        //System.out.println("Server is ticking!");
    }
}
