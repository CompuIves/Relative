package com.ives.relative.network.packets.input;

import com.badlogic.gdx.utils.Array;
import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 11/12/2014.
 * This packet contains the input at the given timestep.
 * <p/>
 * SENT BY CLIENT
 * HANDLED BY SERVER
 */
public class CommandPacket extends UpdatePacket {
    public byte[] inputsPressed;
    public byte[] inputsReleased;

    public CommandPacket() {
    }

    public CommandPacket(int sequence, long entityID, Array<Byte> pressed, Array<Byte> released) {
        super(sequence, entityID);
        this.inputsPressed = new byte[pressed.size];
        this.inputsReleased = new byte[released.size];
        for (int i = 0; i < pressed.size; i++) {
            this.inputsPressed[i] = pressed.get(i);
        }
        for (int i = 0; i < released.size; i++) {
            this.inputsReleased[i] = released.get(i);
        }
    }
}
