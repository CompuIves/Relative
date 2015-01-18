package com.ives.relative.universe.chunks.chunkloaders;

import com.artemis.managers.UuidEntityManager;
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
    public void loadChunk(Chunk chunk) {
        super.commonLoad(chunk);
        clientNetwork.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestChunk(chunk.x, chunk.y));
    }

    @Override
    public void unLoadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        super.commonUnLoad(chunk, uuidEntityManager);
    }
}
