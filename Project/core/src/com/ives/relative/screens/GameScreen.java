package com.ives.relative.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.world.Earth;
import com.ives.relative.world.Planet;
import com.ives.relative.world.WorldManager;

/**
 * Created by Ives on 12/1/2014.
 */
public class GameScreen implements Screen {
    WorldManager worldManager;
    Planet currentPlanet;
    OrthographicCamera camera;

    Box2DDebugRenderer debugRenderer;

    public GameScreen(OrthographicCamera camera) {
        worldManager = new WorldManager();
        this.camera = camera;
        currentPlanet = worldManager.addPlanet("earth", new Earth());

        debugRenderer = new Box2DDebugRenderer();
    }

    public Planet getCurrentPlanet() {
        return currentPlanet;
    }

    @Override
    public void render(float delta) {
        worldManager.processSteps(delta);
        debugRenderer.render(currentPlanet.getWorld(), camera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
