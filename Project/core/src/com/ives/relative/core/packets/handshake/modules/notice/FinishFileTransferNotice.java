package com.ives.relative.core.packets.handshake.modules.notice;

import com.ives.relative.assets.modules.Module;

import java.util.List;

/**
 * Created by Ives on 10/12/2014.
 */
public class FinishFileTransferNotice {
    public List<Module> moduleList;

    public FinishFileTransferNotice() {

    }

    public FinishFileTransferNotice(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

}
