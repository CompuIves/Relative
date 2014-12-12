package com.ives.relative.core.server;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.ServerPlayerManager;
import com.ives.relative.managers.assets.modules.ModuleManager;

import java.io.IOException;

/**
 * Created by Ives on 12/12/2014.
 */
public class ServerManager extends GameManager {
    /**
     * This GameManager is a server.
     */
    public ServerManager() {
        super(true);
        try {
            network = new ServerNetwork(this, new Server());
            registerSystems();
            registerManagers();
            world.setManager(this);
            world.initialize();
            createPlanet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerSystems() {
        super.registerSystems();
    }

    @Override
    public void registerManagers() {
        super.registerManagers();
        world.setManager(new ServerPlayerManager());

        ModuleManager moduleManager = world.getManager(ModuleManager.class);
        moduleManager.loadModules();
        moduleManager.zipAllModules();
    }

    private void createPlanet() {
        PlanetManager planetManager = world.getManager(PlanetManager.class);
        Entity planet = planetManager.createNewPlanet("earth", "Earth", "ivesiscool", new World(new Vector2(0, -10), true), 8, 10);
        planetManager.generateTerrain(planet);
    }
}
