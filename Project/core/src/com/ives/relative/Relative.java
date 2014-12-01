package com.ives.relative;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ives.relative.input.InputManager;
import com.ives.relative.screens.GameScreen;

public class Relative extends Game {
	SpriteBatch batch;
	OrthographicCamera camera;

	public GameScreen gameScreen;
	public InputManager inputManager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		Gdx.graphics.setDisplayMode(1280, 720, false);

		inputManager = new InputManager(this);
		camera = new OrthographicCamera(1280, 720);
		camera.setToOrtho(true, 1280, 720);
		gameScreen = new GameScreen(camera);
		setScreen(gameScreen);
	}

	@Override
	public void render () {
		super.render();
		inputManager.updateInput();
		/*
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		*/
	}
}
