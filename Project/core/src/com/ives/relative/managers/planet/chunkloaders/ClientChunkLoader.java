package com.ives.relative.managers.planet.chunkloaders;

import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.managers.planet.Chunk;
import com.ives.relative.network.packets.handshake.planet.RequestChunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ClientChunkLoader implements ChunkLoader {
    protected ClientNetwork clientNetwork;

    public ClientChunkLoader(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
    }

    @Override
    public void loadChunkInfo(Chunk chunk) {
        clientNetwork.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestChunk(chunk.x, chunk.y, chunk.planet));
    }
}
