package com.ives.relative.core.packets.updates;

import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 11/12/2014.
 */
public abstract class UpdatePacket extends Packet {
    int sequence;

    public UpdatePacket() {
    }

    public UpdatePacket(int sequence) {
        this.sequence = sequence;
    }
}
