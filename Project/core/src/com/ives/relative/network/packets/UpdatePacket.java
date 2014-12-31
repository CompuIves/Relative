package com.ives.relative.network.packets;

/**
 * Created by Ives on 14/12/2014.
 * This packet is purely for entity based updates live through the game.
 */
public class UpdatePacket extends BasePacket {
    public int entityID;

    public UpdatePacket() {
    }

    public UpdatePacket(int sequence, int entityID) {
        super(sequence);
        this.entityID = entityID;
    }
}
