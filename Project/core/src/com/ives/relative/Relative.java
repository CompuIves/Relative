package com.ives.relative;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ives.relative.core.GameManager;

public class Relative extends Game {
	public SpriteBatch batch;
	public OrthographicCamera camera;

	public GameManager clientGameManager;
	public GameManager serverGameManager;



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


		//Add the server first, otherwise the client starts searching for a server while the server hasn't even started yet.
		serverGameManager = new GameManager(this, true);
		clientGameManager = new GameManager(this, false);
	}

	@Override
	public void render () {
		super.render();

		//TODO is this the way to do it?
		if(clientGameManager != null)
			clientGameManager.render(Gdx.graphics.getDeltaTime());

		if(serverGameManager != null)
			serverGameManager.render(Gdx.graphics.getDeltaTime());
	}
}
