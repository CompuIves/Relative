package com.ives.relative.network.packets.input;

import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 17/12/2014.
 */
public class CommandPressPacket extends UpdatePacket {
    public boolean pressed;
    public byte command;

    public CommandPressPacket() {
    }

    public CommandPressPacket(int sequence, long entityID, byte command, boolean pressed) {
        super(sequence, entityID);

        this.command = command;
        this.pressed = pressed;
    }


}
