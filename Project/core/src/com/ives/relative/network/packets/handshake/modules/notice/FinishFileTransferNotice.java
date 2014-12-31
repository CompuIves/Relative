package com.ives.relative.network.packets.handshake.modules.notice;

import com.ives.relative.managers.assets.modules.Module;

import java.util.List;

/**
 * Created by Ives on 10/12/2014.
 * This notice serves as purpose to let the client know the file transfer has been finished. It contains the modulelist
 * of the activated modules on the server.
 */
public class FinishFileTransferNotice {
    public List<Module> moduleList;

    public FinishFileTransferNotice() {

    }

    public FinishFileTransferNotice(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

}
