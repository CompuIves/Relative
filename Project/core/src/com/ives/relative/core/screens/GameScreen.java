package com.ives.relative.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.ives.relative.planet.PlanetFactory;
import com.ives.relative.planet.TerrainGenerator;

/**
 * Created by Ives on 12/1/2014.
 */
public class GameScreen implements Screen {
    OrthographicCamera camera;

    BitmapFont fpsFont;

    SpriteBatch batch;
    PlanetFactory planetFactory;
    TerrainGenerator terrainGenerator;

    public GameScreen(OrthographicCamera camera, SpriteBatch batch) {
        this.camera = camera;
        this.batch = batch;

        fpsFont = new BitmapFont();

        terrainGenerator = new TerrainGenerator();
        planetFactory = new PlanetFactory(terrainGenerator);

        planetFactory.createPlanet("earth", "Earth", new Vector2(0, -10), 8, 3);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.9f, 1f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT );
        batch.setProjectionMatrix(camera.combined);
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
