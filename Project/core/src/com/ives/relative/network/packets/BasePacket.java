package com.ives.relative.network.packets;

/**
 * Created by Ives on 11/12/2014.
 */
public abstract class BasePacket {
    public int sequence;
    public int connection;

    public BasePacket() {
    }

    public BasePacket(int sequence) {
        this.sequence = sequence;
    }
}
