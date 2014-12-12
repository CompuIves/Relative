package com.ives.relative.core.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.ives.relative.core.GameManager;
import com.ives.relative.systems.Box2DDebugRendererSystem;
import com.ives.relative.systems.InputSystem;
import com.ives.relative.systems.RenderSystem;

import java.io.IOException;

/**
 * Created by Ives on 12/12/2014.
 * The official manager of the client
 */
public class ClientManager extends GameManager implements Screen {
    SpriteBatch batch;
    OrthographicCamera camera;

    /**
     * This GameManager is a client.
     */
    public ClientManager() {
        super(false);
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        registerSystems();
        registerManagers();
        world.setManager(this);
        world.initialize();
        try {
            this.network = new ClientNetwork(this, new Client());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerSystems() {
        super.registerSystems();
        world.setSystem(new RenderSystem(batch, camera));

        InputSystem inputSystem = new InputSystem();
        Gdx.input.setInputProcessor(inputSystem);
        world.setSystem(inputSystem);

        world.setSystem(new Box2DDebugRendererSystem(camera));
        //game.engine.addSystem(new ClientSystem(1/20f, network));
    }

    @Override
    public void registerManagers() {
        super.registerManagers();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.9f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        super.render(delta);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 30f, height / 30f);
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
