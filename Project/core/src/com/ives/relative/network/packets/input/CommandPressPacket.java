package com.ives.relative.network.packets.input;

import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 17/12/2014.
 * A simple packet which gets sent when the player presses a button or releases a button.
 *
 * SENT BY CLIENT
 */
public class CommandPressPacket extends UpdatePacket {
    public boolean pressed;
    public byte command;

    public CommandPressPacket() {
    }

    public CommandPressPacket(int sequence, int entityID, byte command, boolean pressed) {
        super(sequence, entityID);

        this.command = command;
        this.pressed = pressed;
    }


}
