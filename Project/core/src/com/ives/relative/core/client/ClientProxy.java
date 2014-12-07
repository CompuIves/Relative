package com.ives.relative.core.client;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.core.client.screens.GameScreen;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.systems.Box2DDebugRendererSystem;
import com.ives.relative.entities.systems.InputSystem;
import com.ives.relative.entities.systems.RenderSystem;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientProxy extends Proxy {
    private GameManager game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private GameScreen gameScreen;

    public ClientProxy(GameManager game, OrthographicCamera camera, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = camera;

        registerSystems();
        gameScreen = new GameScreen(camera, batch);
        game.relative.setScreen(gameScreen);

        network  = new ClientNetwork(game);
    }

    @Override
    public void registerSystems() {
        InputProcessor inputProcessor = new InputSystem(Family.all(InputComponent.class).get());
        Gdx.input.setInputProcessor(inputProcessor);

        game.engine.addSystem(new RenderSystem(batch, camera));
        game.engine.addSystem((EntitySystem) inputProcessor);
        game.engine.addSystem(new Box2DDebugRendererSystem(Family.all(WorldComponent.class).get(), camera));
    }

    public void update(float delta) {
        //System.out.println("Client is ticking!");
    }

}
