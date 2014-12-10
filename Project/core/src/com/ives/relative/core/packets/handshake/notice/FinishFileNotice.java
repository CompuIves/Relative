package com.ives.relative.core.packets.handshake.notice;

import com.ives.relative.assets.modules.Module;

import java.util.List;

/**
 * Created by Ives on 10/12/2014.
 */
public class FinishFileNotice {
    public List<Module> moduleList;

    public FinishFileNotice() {

    }

    public FinishFileNotice(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

}
