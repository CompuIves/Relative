package com.ives.relative.core.packets.updates;

import com.ives.relative.core.GameManager;

/**
 * Created by Ives on 11/12/2014.
 */
public class PositionPacket extends UpdatePacket {

    public PositionPacket(int connectionID, float deltaTime, int sequence) {
        super(connectionID, deltaTime, sequence);
    }

    @Override
    public void response(GameManager game) {

    }
}
