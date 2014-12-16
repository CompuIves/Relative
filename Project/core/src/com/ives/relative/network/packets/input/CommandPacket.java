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
    public byte[] commandList;
    public float[] deltaTime;

    public CommandPacket() {
    }

    public CommandPacket(int sequence, Array<Byte> commandList, Array<Float> deltaTime, long entityID) {
        super(sequence, entityID);
        this.commandList = new byte[commandList.size];
        this.deltaTime = new float[deltaTime.size];
        for (int i = 0; i < commandList.size; i++) {
            this.commandList[i] = commandList.get(i);
            this.deltaTime[i] = deltaTime.get(i);
        }
    }
}
