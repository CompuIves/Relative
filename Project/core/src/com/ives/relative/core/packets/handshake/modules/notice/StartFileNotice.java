package com.ives.relative.core.packets.handshake.modules.notice;

/**
 * Created by Ives on 10/12/2014.
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
