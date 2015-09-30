package com.ives.relative.universe.chunks.builders;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;
import com.ives.relative.utils.SimplexNoise;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Creates a square planet
 */
public class SquarePlanet extends ChunkBuilder {
    Vector2 originalGravity;

    /**
     * Creates a square planet with correct gravity and sizes.
     * @param space
     * @param tileManager
     * @param uuidEntityManager
     * @param size
     * @param originalGravity
     */
    public SquarePlanet(Space space, TileManager tileManager, UuidEntityManager uuidEntityManager, int size, Vector2 originalGravity) {
        super(space, tileManager, uuidEntityManager);

        this.originalGravity = originalGravity;
    }

    @Override
    public Chunk buildChunk(int x, int y, int width, int height, boolean isEdge) {
        return new Chunk(space, x, y, width, height, 0, isEdge);
    }

    @Override
    public void generateTerrain(Chunk chunk) {
        int startX = chunk.x;
        int startY = chunk.y;
        int endX = startX + chunk.width;
        int endY = startY + chunk.height;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (y <= Math.round(SimplexNoise.noise(x / 3, 0) * 1.5)) {
                    Entity tile;
                    if (y == Math.round(SimplexNoise.noise(x / 3, 0) * 1.5)) {
                        tile = tileManager.createTile(chunk.space, x, y, 0, "grass", false);
                    } else {
                        tile = tileManager.createTile(chunk.space, x, y, 0, "dirt", false);
                    }
                    chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
                }
            }
        }

        tileManager.generateTileBodies(chunk);
    }
}
