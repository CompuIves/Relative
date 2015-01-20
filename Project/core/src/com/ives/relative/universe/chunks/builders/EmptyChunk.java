package com.ives.relative.universe.chunks.builders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 18/1/2015.
 */
public class EmptyChunk extends ChunkBuilder {

    public EmptyChunk(UniverseBody universeBody, TileManager tileManager, UuidEntityManager uuidEntityManager) {
        super(universeBody, tileManager, uuidEntityManager);
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
