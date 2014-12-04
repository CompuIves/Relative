package com.ives.relative.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.ives.relative.Relative;
import com.ives.relative.core.screens.GameScreen;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.systems.RenderSystem;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.planet.tiles.TileManager;

/**
 * Created by Ives on 4/12/2014.
 */
public class GameManager {

    public static Engine engine;
    public static TileManager tileManager;
    public GameScreen gameScreen;

    Relative relative;

    public static float PHYSICS_ITERATIONS = 1/45f;

    public GameManager(Relative relative) {
        tileManager = new TileManager();
        this.relative = relative;
        engine = new Engine();
        registerSystems();

        clientProcess();
    }

    private void clientProcess() {
        gameScreen = new GameScreen(relative.camera, relative.batch);
        relative.setScreen(gameScreen);
    }

    private void registerSystems() {
        engine.addSystem(new RenderSystem(relative.batch, relative.camera));
        engine.addSystem(new WorldSystem(Family.all(WorldComponent.class).get(), PHYSICS_ITERATIONS));
    }

    public void render(float delta) {
        engine.update(delta);
    }
}
