package com.ives.relative.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ives.relative.Relative;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		if(arg.length > 0) {
			if (arg[0].equalsIgnoreCase("server")) {
				Relative.MODE = Relative.Mode.Server;
			} else if (arg[0].equalsIgnoreCase("client")) {
				Relative.MODE = Relative.Mode.Client;
			} else {
				Relative.MODE = Relative.Mode.Both;
			}
		} else {
			Relative.MODE = Relative.Mode.Both;
		}

		new LwjglApplication(new Relative(), config);
	}
}
