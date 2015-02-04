package com.ives.relative.universe.chunks.chunkloaders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 7/1/2015.
 */
public class ServerChunkLoader extends ChunkLoader {

    @Override
    public void requestChunk(Chunk chunk) {
        super.loadChunk(chunk);
        //Reading from file
    }

    @Override
    public void unloadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        //if there are no players in the chunk left.
        if (chunk.getLoadedByPlayersSize() == 0) {
            super.unloadChunk(chunk, uuidEntityManager);
        }
    }

    @Override
    protected void loadTileLegend() {

    }
}
