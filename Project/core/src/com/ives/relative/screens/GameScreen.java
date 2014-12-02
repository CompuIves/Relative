package com.ives.relative.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.ives.relative.entities.tiles.Tile;
import com.ives.relative.world.planets.Earth;
import com.ives.relative.world.Planet;
import com.ives.relative.world.WorldManager;

import java.util.Map;

/**
 * Created by Ives on 12/1/2014.
 */
public class GameScreen implements Screen {
    WorldManager worldManager;
    Planet currentPlanet;
    OrthographicCamera camera;

    SpriteBatch batch;

    Box2DDebugRenderer debugRenderer;

    public GameScreen(OrthographicCamera camera, SpriteBatch batch) {
        worldManager = new WorldManager();
        this.camera = camera;
        this.batch = batch;
        currentPlanet = worldManager.addPlanet("earth", new Earth());

        debugRenderer = new Box2DDebugRenderer();
    }

    public Planet getCurrentPlanet() {
        return currentPlanet;
    }

    @Override
    public void render(float delta) {
        worldManager.processSteps(delta);
        Gdx.gl.glClearColor(0.5f, 0.9f, 1f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderWorld(batch);
        batch.end();

        debugRenderer.render(currentPlanet.getWorld(), camera.combined);
    }

    private void renderWorld(SpriteBatch batch) {

        //TODO FIND OUT CAMERA POSITIONING
        int startX = (int) 0 - 3;
        int endX = (int) (0 + camera.viewportWidth + 3);
        int startY = (int) 0 - 3;
        int endY = (int) (0 + camera.viewportWidth + 3);

        for(int row = startY; row < endY; row++) {
            for(int column = startX; column < endX; column++) {
                Tile tile = currentPlanet.getTile(column, row);
                if(tile != null)
                    tile.draw(batch);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 15f, height / 15f);
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
