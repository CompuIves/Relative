package com.ives.relative.network.packets.handshake.planet;

import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.Chunk;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;

import java.util.UUID;

/**
 * Created by Ives on 7/1/2015.
 * Sends the information of a chunk to the client.
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestChunk extends ResponsePacket {
    int index;
    String planet;

    public RequestChunk() {
    }

    public RequestChunk(int index, String planet) {
        this.index = index;
        this.planet = planet;
    }

    @Override
    public void response(GameManager game) {
        Chunk chunk = game.world.getManager(ChunkManager.class).getChunk(index, planet);
        game.network.sendObjectTCP(connection, new ChunkPacket(chunk));

        NetworkSendSystem networkSendSystem = game.world.getSystem(NetworkSendSystem.class);
        for (UUID eUUID : chunk.entities) {
            networkSendSystem.sendEntity(connection, eUUID);
        }
    }
}
