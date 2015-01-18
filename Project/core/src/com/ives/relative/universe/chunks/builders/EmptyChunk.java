package com.ives.relative.universe.chunks.builders;

import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 18/1/2015.
 */
public class EmptyChunk extends ChunkBuilder {
    public EmptyChunk(UniverseBody universeBody) {
        super(universeBody);
    }

    @Override
    public Chunk buildChunk(int x, int y) {
        //TODO gravity setting
        return new Chunk(universeBody, x, y, 0, false, 0, 0);
    }

    @Override
    public void generateTerrain(Chunk chunk) {
        //Do nothing, chunk empty
    }
}
