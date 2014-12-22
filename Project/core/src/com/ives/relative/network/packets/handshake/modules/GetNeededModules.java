package com.ives.relative.network.packets.handshake.modules;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.managers.assets.modules.Module;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.network.packets.ResponsePacket;

import java.util.List;

/**
 * Created by Ives on 9/12/2014.
 * <p/>
 * Connection is accepted. Now the server needs the list of the modules the client has.
 * HANDLED BY CLIENT
 */
public class GetNeededModules extends ResponsePacket {
    List<Module> modules;

    public GetNeededModules() {
    }

    @Override
    public void response(final GameManager game) {
        //Use runnable otherwise the proxy = null
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println("Breakie breakie");
                modules = game.world.getManager(ModuleManager.class).getModules();
                game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestModulesPacket(modules));
            }
        });
    }
}
