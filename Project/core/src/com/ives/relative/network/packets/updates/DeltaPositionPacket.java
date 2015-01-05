package com.ives.relative.network.packets.updates;

import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 5/1/2015.
 */
public class DeltaPositionPacket extends UpdatePacket {
    public float dx, dy;

    public DeltaPositionPacket() {
    }

    public DeltaPositionPacket(int sequence, int entityID, float dx, float dy) {
        super(sequence, entityID);
        this.dx = dx;
        this.dy = dy;
    }
}
