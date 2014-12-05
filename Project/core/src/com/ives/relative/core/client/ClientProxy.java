package com.ives.relative.core.client;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.Proxy;
import com.ives.relative.core.client.screens.GameScreen;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.systems.InputSystem;
import com.ives.relative.entities.systems.RenderSystem;
import com.ives.relative.entities.systems.WorldSystem;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientProxy extends Proxy {
    GameManager game;
    SpriteBatch batch;
    OrthographicCamera camera;

    public GameScreen gameScreen;

    public ClientProxy(GameManager game, OrthographicCamera camera, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = camera;

        network  = new ClientNetwork(game);

        registerSystems();
        gameScreen = new GameScreen(camera, batch);
        game.relative.setScreen(gameScreen);
    }

    @Override
    public void registerSystems() {
        game.engine.addSystem(new RenderSystem(batch, camera));
        game.engine.addSystem(new InputSystem());
    }

    public void update(float delta) {
        //System.out.println("Client is ticking!");
    }

}
