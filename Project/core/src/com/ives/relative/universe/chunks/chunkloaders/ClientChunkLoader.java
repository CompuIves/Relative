package com.ives.relative.universe.chunks.chunkloaders;

import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.network.packets.handshake.planet.RequestChunk;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ClientChunkLoader extends ChunkLoader {
    protected ClientNetwork clientNetwork;

    public ClientChunkLoader(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
    }

    @Override
    public void requestChunk(Chunk chunk) {
        clientNetwork.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestChunk(chunk.x, chunk.y));
    }

    @Override
    void loadTileLegend() {

    }
}
