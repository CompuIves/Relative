package com.ives.relative.core.packets.handshake;

import com.badlogic.gdx.Gdx;
import com.ives.relative.assets.modules.Module;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

import java.util.List;

/**
 * Created by Ives on 9/12/2014.
 *
 * Connection is accepted. Now the server needs the list of the modules the client has.
 * HANDLED BY CLIENT
 */
public class GetNeededModules implements Packet {
    List<Module> modules;

    public GetNeededModules() {
    }

    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                modules = game.moduleManager.getModules();
                game.proxy.network.sendObjectTCPToServer(new RequestModules(modules));
            }
        });
    }
}
