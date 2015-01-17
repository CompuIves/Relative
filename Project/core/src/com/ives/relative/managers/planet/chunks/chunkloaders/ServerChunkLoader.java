package com.ives.relative.managers.planet.chunks.chunkloaders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.managers.planet.PlanetGenerator;
import com.ives.relative.managers.planet.chunks.Chunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ServerChunkLoader extends ChunkLoader {

    @Override
    public void loadChunk(Chunk chunk, PlanetGenerator planetGenerator) {
        super.commonLoad(chunk, planetGenerator);
        //Reading from file
    }

    @Override
    public void unLoadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        //if there are no players in the chunk left.
        if (chunk.players == 0) {
            super.commonUnLoad(chunk, uuidEntityManager);
        }
    }
}
