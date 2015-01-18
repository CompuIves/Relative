package com.ives.relative.universe.chunks.builders;

import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Builds a planet from the properties it has. This means it will allocate chunks
 */
public abstract class ChunkBuilder {
    protected UniverseBody universeBody;

    public ChunkBuilder(UniverseBody universeBody) {
        this.universeBody = universeBody;
    }

    public abstract Chunk buildChunk(int x, int y);

    public abstract void generateTerrain(Chunk chunk);
}
