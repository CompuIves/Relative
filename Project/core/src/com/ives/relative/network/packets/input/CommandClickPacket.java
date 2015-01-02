package com.ives.relative.network.packets.input;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Ives on 2/1/2015.
 */
public class CommandClickPacket extends CommandPressPacket {
    public float x, y;

    public CommandClickPacket() {
    }

    public CommandClickPacket(int sequence, int entityID, byte command, boolean pressed, Vector2 pos) {
        super(sequence, entityID, command, pressed);
        x = pos.x;
        y = pos.y;
    }
}
