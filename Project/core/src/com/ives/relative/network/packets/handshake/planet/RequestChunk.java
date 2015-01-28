package com.ives.relative.network.packets.handshake.planet;

import com.ives.relative.core.GameManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 7/1/2015.
 * Sends the information of a chunk to the client.
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestChunk extends ResponsePacket {
    int x, y;
    String universeBody;

    public RequestChunk() {
    }

    public RequestChunk(String universeBody, int x, int y) {
        this.x = x;
        this.y = y;
        this.universeBody = universeBody;
    }

    @Override
    public void response(GameManager game) {
        Chunk chunk = game.world.getManager(UniverseManager.class).getUniverseBody(universeBody).getChunk(x, y);
        NetworkSendSystem networkSendSystem = game.world.getSystem(NetworkSendSystem.class);

        ChunkPacket packet = networkSendSystem.generateFullChunkPacket(chunk);
        game.network.sendObjectTCP(connection, packet);
    }
}
