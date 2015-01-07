package com.ives.relative.network.packets.handshake.planet;

import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 7/1/2015.
 */
public class RequestChunk extends ResponsePacket {
    float x;
    String planet;

    public RequestChunk() {
    }

    public RequestChunk(float x, String planet) {
        this.x = x;
        this.planet = planet;
    }

    @Override
    public void response(GameManager game) {
        game.network.sendObjectTCP(connection, new ChunkPacket(game.world.getManager(ChunkManager.class).getChunk(x, planet)));
    }
}
