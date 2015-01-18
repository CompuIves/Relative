package com.ives.relative.universe.chunks.chunkloaders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ServerChunkLoader extends ChunkLoader {

    @Override
    public void loadChunk(Chunk chunk) {
        super.commonLoad(chunk);
        //Reading from file
    }

    @Override
    public void unLoadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        //if there are no players in the chunk left.
        if (chunk.getPlayerAmount() == 0) {
            super.commonUnLoad(chunk, uuidEntityManager);
        }
    }
}
