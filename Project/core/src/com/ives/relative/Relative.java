package com.ives.relative;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ives.relative.core.GameManager;

public class Relative extends Game {
	public SpriteBatch batch;
	public OrthographicCamera camera;

	public GameManager gameManager;

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.setToOrtho(false, width / 30f, height / 30f);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		Gdx.graphics.setDisplayMode(1280, 720, false);

		gameManager = new GameManager(this);
	}

	@Override
	public void render () {
		super.render();

		//TODO is this the way to do it?
		if(gameManager != null)
			gameManager.render(Gdx.graphics.getDeltaTime());
	}
}
