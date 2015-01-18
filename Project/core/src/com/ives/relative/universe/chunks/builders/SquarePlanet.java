package com.ives.relative.universe.chunks.builders;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Creates a square planet
 */
public class SquarePlanet extends ChunkBuilder {
    Vector2 originalGravity;

    public SquarePlanet(UniverseBody universeBody, Vector2 originalGravity) {
        super(universeBody);

        this.originalGravity = originalGravity;
    }

    @Override
    public Chunk buildChunk(int x, int y) {
        return null;
    }

    @Override
    public void generateTerrain(Chunk chunk) {

    }
}
