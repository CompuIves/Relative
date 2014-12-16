package com.ives.relative.network.packets.input;

import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 16/12/2014.
 */
public class HookUpCommandPacket extends UpdatePacket {
    public byte command;

    public HookUpCommandPacket() {
    }

    public HookUpCommandPacket(int sequence, long entityID, byte command) {
        super(sequence, entityID);
        this.command = command;
    }
}
