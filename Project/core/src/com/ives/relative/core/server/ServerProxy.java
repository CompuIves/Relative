package com.ives.relative.core.server;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.planet.PlanetFactory;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {
    GameManager game;
    PlanetFactory planetFactory;

    public ServerProxy(GameManager game) {
        this.game = game;

        network = new ServerNetwork(game);

        planetFactory = new PlanetFactory(game);
        planetFactory.createPlanet("earth", "Earth", new Vector2(0, -10), 8, 3);
        registerSystems();
    }

    public void registerSystems() {

    }

    public void update(float delta) {
        //System.out.println("Server is ticking!");
    }
}
