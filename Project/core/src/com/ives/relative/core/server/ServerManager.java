package com.ives.relative.core.server;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.managers.planet.chunkloaders.ServerChunkLoader;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.systems.Box2DDebugRendererSystem;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.systems.server.ServerNetworkSystem;

import java.io.IOException;

/**
 * Created by Ives on 12/12/2014.
 * Manager specific for the server, adds server specific systems and managers to the world.
 */
public class ServerManager extends GameManager {
    OrthographicCamera camera;

    /**
     * This GameManager is a server.
     */
    public ServerManager() {
        super(true);
        try {
            camera = new OrthographicCamera(Gdx.graphics.getWidth() / 20f, Gdx.graphics.getHeight() / 20f);
            network = new ServerNetwork(this, new Server(16384, 4096));
            super.world = new com.artemis.World();
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
        world.setSystem(new ServerNetworkSystem((ServerNetwork) network));
        world.setSystem(new Box2DDebugRendererSystem(camera));
        world.setSystem(new NetworkSendSystem((ServerNetwork) network));
    }

    @Override
    public void registerManagers() {
        super.registerManagers();
        world.setManager(new ServerPlayerManager());
        world.setManager(new ChunkManager(new ServerChunkLoader()));

        ModuleManager moduleManager = world.getManager(ModuleManager.class);
        moduleManager.loadModules();
        moduleManager.zipAllModules();
    }

    private void createPlanet() {
        PlanetManager planetManager = world.getManager(PlanetManager.class);
        Entity planet = planetManager.createNewPlanet("earth", "Earth", "ivesiscool", new Vector2(0, -10), 384, 10, 10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }
}
