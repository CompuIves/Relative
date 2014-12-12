package com.ives.relative.core.packets.handshake.modules;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.managers.assets.modules.Module;

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
    public void response(final GameManager game) {
        //Use runnable otherwise the proxy = null
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                modules = game.moduleManager.getModules();
                game.proxy.network.sendObjectTCPToServer(new RequestModulesPacket(modules, game.proxy.network.connectionID));
            }
        });
    }
}
