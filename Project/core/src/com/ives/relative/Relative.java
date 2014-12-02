package com.ives.relative;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ives.relative.input.InputManager;
import com.ives.relative.screens.GameScreen;

public class Relative extends Game {
	SpriteBatch batch;
	public OrthographicCamera camera;

	public Engine engine;

	public GameScreen gameScreen;
	public InputManager inputManager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		Gdx.graphics.setDisplayMode(1280, 720, false);

		engine = new Engine();

		inputManager = new InputManager(this);
		camera = new OrthographicCamera(1280 / 15f, 720 / 15f);
		gameScreen = new GameScreen(camera, batch);
		setScreen(gameScreen);


	}

	@Override
	public void render () {
		super.render();
		camera.update();
		engine.update(Gdx.graphics.getDeltaTime());
		inputManager.updateInput();
	}
}
