package com.ives.relative.network.packets.handshake.planet;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ives on 7/1/2015.
 * Contains the vital information of a chunk
 */
public class ChunkPacket extends BasePacket {
    public List<CreateEntityPacket> entities;
    public HashMap<Vector2, Integer> changedTiles;
    public int x, y;

    public String universeBody;

    public ChunkPacket() {
    }

    public ChunkPacket(int x, int y, String universeBody, List<CreateEntityPacket> entities, HashMap<Vector2, Integer> changedTiles) {
        this.x = x;
        this.y = y;
        this.entities = entities;
        this.changedTiles = changedTiles;
        this.universeBody = universeBody;
    }
}
