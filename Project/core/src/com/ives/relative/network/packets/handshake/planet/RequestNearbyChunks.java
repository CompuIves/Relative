package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.Chunk;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 7/1/2015.
 * Requests chunks in x range
 * HANDLED BY SERVER
 */
public class RequestNearbyChunks extends ResponsePacket {
    public RequestNearbyChunks() {
    }

    @Override
    public void response(GameManager game) {
        Entity player = game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection);
        Array<Chunk> chunks = game.world.getManager(ChunkManager.class).getChunksSurroundingEntity(player);
        for (Chunk chunk : chunks) {
            game.network.sendObjectTCP(connection, new ChunkPacket(chunk));
        }
    }
}
