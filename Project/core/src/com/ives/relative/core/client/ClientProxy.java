package com.ives.relative.core.client;

import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.core.client.screens.GameScreen;
import com.ives.relative.systems.Box2DDebugRendererSystem;
import com.ives.relative.systems.InputSystem;
import com.ives.relative.systems.RenderSystem;

/**
 * Created by Ives on 4/12/2014.
 * The client will only use this, so the client side systems will be activated here
 */
public class ClientProxy extends Proxy {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private GameScreen gameScreen;

    public ClientProxy(GameManager game, OrthographicCamera camera, SpriteBatch batch, World entityWorld) {
        ClientProxy.game = game;
        ClientProxy.world = entityWorld;
        this.batch = batch;
        this.camera = camera;

        gameScreen = new GameScreen(camera, batch);
        game.relative.setScreen(gameScreen);

        Client client = new Client();
        network  = new ClientNetwork(game, client);
        game.registerCommonSystems(world);
        game.registerCommonManagers(world);
        registerSystems();
        world.initialize();
    }

    @Override
    public void registerSystems() {
        game.entityWorld.setSystem(new RenderSystem(batch, camera));

        InputSystem inputSystem = new InputSystem();
        Gdx.input.setInputProcessor(inputSystem);
        game.entityWorld.setSystem(inputSystem);

        game.entityWorld.setSystem(new Box2DDebugRendererSystem(camera));
        //game.engine.addSystem(new ClientSystem(1/20f, network));
    }

    public void update(float delta) {
        //System.out.println("Client is ticking!");
    }

}
