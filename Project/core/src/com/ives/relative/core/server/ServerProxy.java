package com.ives.relative.core.server;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {
    GameManager game;

    public ServerProxy(GameManager game) {
        this.game = game;
        network = new ServerNetwork(game);
        registerSystems();
    }

    public void registerSystems() {

    }

    public void update(float delta) {
        //System.out.println("Server is ticking!");
    }
}
