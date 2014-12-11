package com.ives.relative.core.client;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.core.client.screens.GameScreen;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.planet.WorldComponent;
import com.ives.relative.entities.systems.Box2DDebugRendererSystem;
import com.ives.relative.entities.systems.InputSystem;
import com.ives.relative.entities.systems.RenderSystem;

/**
 * Created by Ives on 4/12/2014.
 * The client will only use this, so the client side systems will be activated here
 */
public class ClientProxy extends Proxy {
    public static GameManager game;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private GameScreen gameScreen;

    public ClientProxy(GameManager game, OrthographicCamera camera, SpriteBatch batch) {
        ClientProxy.game = game;
        this.batch = batch;
        this.camera = camera;

        gameScreen = new GameScreen(camera, batch);
        game.relative.setScreen(gameScreen);

        Client client = new Client();
        network  = new ClientNetwork(game, client);
        registerSystems();
    }

    @Override
    public void registerSystems() {

        game.engine.addSystem(new RenderSystem(batch, camera, game.engine));

        InputSystem inputSystem = new InputSystem(Family.all(InputComponent.class).get());
        Gdx.input.setInputProcessor(inputSystem);
        game.engine.addSystem(inputSystem);

        game.engine.addSystem(new Box2DDebugRendererSystem(Family.all(WorldComponent.class).get(), camera));
        //game.engine.addSystem(new ClientSystem(1/20f, network));
    }

    public void update(float delta) {
        //System.out.println("Client is ticking!");
    }

}
