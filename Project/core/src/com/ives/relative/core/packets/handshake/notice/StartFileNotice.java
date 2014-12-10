package com.ives.relative.core.packets.handshake.notice;

/**
 * Created by Ives on 10/12/2014.
 */
public class StartFileNotice {
    public int length;

    public StartFileNotice() {
    }

    public StartFileNotice(int length) {
        this.length = length;
    }
}
