package com.ives.relative.core.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Ives on 12/1/2014.
 */
public class GameScreen implements Screen {
    OrthographicCamera camera;

    BitmapFont fpsFont;

    SpriteBatch batch;

    public GameScreen(OrthographicCamera camera, SpriteBatch batch) {
        this.camera = camera;
        this.batch = batch;

        fpsFont = new BitmapFont();
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
