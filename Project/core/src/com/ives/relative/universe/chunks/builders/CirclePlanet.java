package com.ives.relative.universe.chunks.builders;

import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 2/2/2015.
 */
public class CirclePlanet extends ChunkBuilder {

    public CirclePlanet(UniverseBody universeBody, TileManager tileManager, UuidEntityManager uuidEntityManager) {
        super(universeBody, tileManager, uuidEntityManager);
    }

    @Override
    public Chunk buildChunk(int x, int y, int width, int height, boolean isEdge) {
        return new Chunk(universeBody, x, y, 0, width, height, 0, isEdge, 0, -10);
    }

    @Override
    public void generateTerrain(Chunk chunk) {
        //TODO write round code
    }
}
