package com.ives.relative.network.packets.updates;

import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 5/1/2015.
 */
public class PositionHeartbeat extends UpdatePacket {
    public float x, y;
    public float vx, vy;

    public PositionHeartbeat() {
        super();
    }

    public PositionHeartbeat(int sequence, int entityID, float x, float y, float vx, float vy) {
        super(sequence, entityID);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Special equals override because this will make it easier to sort arrays, only one heartbeat would have to be processed.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PositionHeartbeat) {
            PositionHeartbeat p2 = (PositionHeartbeat) obj;
            if (p2.entityID == entityID) {
                return true;
            }
        }
        return false;
    }
}
