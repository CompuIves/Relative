package com.ives.relative.core.packets.updates;

import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 11/12/2014.
 */
public abstract class UpdatePacket implements Packet {
    int connectionID;
    float deltaTime;
    int sequence;

    public UpdatePacket() {
    }

    public UpdatePacket(int connectionID, float deltaTime, int sequence) {
        this.connectionID = connectionID;
        this.deltaTime = deltaTime;
        this.sequence = sequence;
    }
}
