package com.ives.relative.core.packets.handshake;

import com.ives.relative.assets.modules.Module;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

import java.util.List;

/**
 * Created by Ives on 9/12/2014.
 * Compares modules and sends the needed modules by the client.
 * <p/>
 * HANDLED FROM SERVER
 */
public class RequestModules implements Packet {
    List<Module> modules;

    public RequestModules() {
    }

    public RequestModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public void handle(GameManager game) {
        List<Module> deltaModules = game.moduleManager.compareModuleLists(modules);

        //TODO add downloader for the delta modules
    }
}
