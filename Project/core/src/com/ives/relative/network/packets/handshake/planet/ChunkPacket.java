package com.ives.relative.network.packets.handshake.planet;

import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;

import java.util.List;

/**
 * Created by Ives on 7/1/2015.
 * Contains the vital information of a chunk
 */
public class ChunkPacket extends BasePacket {
    public List<CreateEntityPacket> entities;
    public int[] changedTiles;
    public int x, y;

    public String universeBody;

    public ChunkPacket() {
    }

    public ChunkPacket(int x, int y, String universeBody, List<CreateEntityPacket> entities, int[] changedTiles) {
        this.x = x;
        this.y = y;
        this.entities = entities;
        this.changedTiles = changedTiles;
        this.universeBody = universeBody;
    }
}
