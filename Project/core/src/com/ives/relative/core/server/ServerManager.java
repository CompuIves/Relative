package com.ives.relative.core.server;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.systems.Box2DDebugRendererSystem;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.systems.server.ServerNetworkSystem;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.chunks.chunkloaders.ServerChunkLoader;
import com.ives.relative.universe.planets.PlanetManager;

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
            createDebugInput();
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
        Entity planet = planetManager.createNewPlanet("earth", "Earth", "ivesiscool", new Vector2(0, -10), 7, 7);
    }

    private void createDebugInput() {
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                world.getManager(ChunkManager.class).loadChunk(pos.x, pos.y);
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.RIGHT) {
                    world.getManager(ChunkManager.class).loadAllChunks();
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                camera.translate(-Gdx.input.getDeltaX() / 10, Gdx.input.getDeltaY() / 10);
                camera.update();
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                camera.viewportHeight = camera.viewportHeight * (1 + amount / 10f);
                camera.viewportWidth = camera.viewportWidth * (1 + amount / 10f);
                camera.update();
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render(delta);
    }
}
