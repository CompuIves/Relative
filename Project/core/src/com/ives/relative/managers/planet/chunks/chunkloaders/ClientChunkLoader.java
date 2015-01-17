package com.ives.relative.managers.planet.chunks.chunkloaders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.managers.planet.PlanetGenerator;
import com.ives.relative.managers.planet.chunks.Chunk;
import com.ives.relative.network.packets.handshake.planet.RequestChunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ClientChunkLoader extends ChunkLoader {
    protected ClientNetwork clientNetwork;

    public ClientChunkLoader(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;
    }

    @Override
    public void loadChunk(Chunk chunk, PlanetGenerator planetGenerator) {
        super.commonLoad(chunk, planetGenerator);
        clientNetwork.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestChunk(chunk.x, chunk.y, chunk.planet));
    }

    @Override
    public void unLoadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        super.commonUnLoad(chunk, uuidEntityManager);
    }
}
