package com.ives.relative.universe.chunks.builders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Builds a planet from the properties it has. This means it will allocate chunks
 */
public abstract class ChunkBuilder {
    protected UniverseBody universeBody;
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public ChunkBuilder(UniverseBody universeBody, TileManager tileManager, UuidEntityManager uuidEntityManager) {
        this.universeBody = universeBody;
        this.tileManager = tileManager;
        this.uuidEntityManager = uuidEntityManager;
    }

    public abstract Chunk buildChunk(int x, int y);

    public abstract void generateTerrain(Chunk chunk);
}
