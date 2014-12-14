package com.ives.relative.network.packets;

/**
 * Created by Ives on 14/12/2014.
 */
public abstract class UpdatePacket extends BasePacket {
    public long entityID;

    public UpdatePacket() {
    }

    public UpdatePacket(int sequence, long entityID) {
        super(sequence);
        this.entityID = entityID;
    }
}
