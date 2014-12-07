package com.ives.relative.core.packets.requests;

import com.ives.relative.assets.modules.Module;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

import java.util.List;

/**
 * Created by Ives on 7/12/2014.
 * Requests modules installed on server, provides a list of installed modules on client
 */
public class RequestModulesPacket implements Packet {
    List<Module> modulesInstalled;

    public RequestModulesPacket() {
    }

    public RequestModulesPacket(List<Module> modulesInstalled) {
        this.modulesInstalled = modulesInstalled;
    }

    @Override
    public void handle(GameManager game) {

    }
}
