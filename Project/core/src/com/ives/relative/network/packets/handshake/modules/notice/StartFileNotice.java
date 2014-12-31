package com.ives.relative.network.packets.handshake.modules.notice;

/**
 * Created by Ives on 10/12/2014.
 * This notice servers as purpose that the client knows a new file is incoming and what the size, name of the module and
 * version of the module is.
 */
public class StartFileNotice {
    public int length;
    public String moduleVersion;
    public String moduleName;

    public StartFileNotice() {
    }

    public StartFileNotice(int length, String moduleName, String moduleVersion) {
        this.length = length;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
    }
}
