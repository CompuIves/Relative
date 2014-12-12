package com.ives.relative;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.server.ServerManager;

public class Relative extends Game {
	public static String VERSION = "1.0";
	public static Relative relative;

	@Override
	public void create () {
		relative = this;
		Gdx.graphics.setDisplayMode(1280, 720, false);


		//Creates two instances, an internal server and a client which connects to the server.
		//Add the server first, otherwise the client starts searching for a server while the server hasn't even started yet.
		ServerManager serverManager = new ServerManager();

		ClientManager clientManager = new ClientManager();
		setScreen(clientManager);
	}
}
