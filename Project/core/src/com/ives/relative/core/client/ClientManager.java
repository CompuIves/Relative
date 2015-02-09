package com.ives.relative.core.client;

import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.esotericsoftware.kryonet.Client;
import com.ives.relative.core.GameManager;
import com.ives.relative.systems.client.ClientNetworkSystem;
import com.ives.relative.systems.client.InputSystem;
import com.ives.relative.systems.client.NetworkReceiveSystem;
import com.ives.relative.systems.client.rendering.RenderSystem;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.chunks.chunkloaders.ClientChunkLoader;

import java.io.IOException;

/**
 * Created by Ives on 12/12/2014.
 * <p></p>
 * The official manager of the client, adds client specific systems and managers to the world.
 */
public class ClientManager extends GameManager implements Screen {
    ModelBatch batch;
    PerspectiveCamera camera;
    /**
     * This GameManager is a client.
     */
    public ClientManager() {
        super(false);
        this.batch = new ModelBatch();
        this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth() / 15f, Gdx.graphics.getHeight() / 15f);
        super.world = new World();
        try {
            this.network = new ClientNetwork(this, new Client());
        } catch (IOException e) {
            e.printStackTrace();
        }
        registerSystems();
        registerManagers();
        world.setManager(this);
        world.initialize();
    }

    @Override
    public void registerSystems() {
        super.registerSystems();
        InputSystem inputSystem = new InputSystem(camera);
        Gdx.input.setInputProcessor(inputSystem);
        world.setSystem(inputSystem);
        world.setSystem(new NetworkReceiveSystem());
        world.setSystem(new ClientNetworkSystem((ClientNetwork) network));

        world.setSystem(new RenderSystem(batch, camera));
        //world.setSystem(new ClientDebugSystem(camera));
    }

    @Override
    public void registerManagers() {
        super.registerManagers();
        world.setManager(new TagManager());
        world.setManager(new ChunkManager(new ClientChunkLoader((ClientNetwork) network)));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        //camera.(false, width / 30f, height / 30f);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
