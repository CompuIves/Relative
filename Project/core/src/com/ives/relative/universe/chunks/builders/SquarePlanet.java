package com.ives.relative.universe.chunks.builders;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Creates a square planet
 */
public class SquarePlanet extends ChunkBuilder {
    Vector2 originalGravity;

    public SquarePlanet(UniverseBody universeBody, TileManager tileManager, UuidEntityManager uuidEntityManager, Vector2 originalGravity) {
        super(universeBody, tileManager, uuidEntityManager);

        this.originalGravity = originalGravity;
    }

    @Override
    public Chunk buildChunk(int x, int y) {
        return new Chunk(universeBody, x, y, 0, true, 0, -10);
    }

    @Override
    public void generateTerrain(Chunk chunk) {
        int chunkSize = ChunkManager.CHUNK_SIZE;

        int startX = chunk.x;
        int startY = chunk.y;
        int endX = startX + chunkSize;
        int endY = startY + chunkSize;

        if (chunk.isLand()) {
            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    Entity tile = tileManager.createTile(x, y, 0, "dirt", false);
                    chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
                }
            }
        }

        chunk.addTile(startX, startY, uuidEntityManager.getUuid(tileManager.createTile(startX, startY, 0, "bedrock", false)));
        chunk.addTile(startX, endY, uuidEntityManager.getUuid(tileManager.createTile(startX, endY, 0, "bedrock", false)));
        chunk.addTile(endX, startY, uuidEntityManager.getUuid(tileManager.createTile(endX, startY, 0, "bedrock", false)));
        chunk.addTile(endX, endY, uuidEntityManager.getUuid(tileManager.createTile(endX, endY, 0, "bedrock", false)));
    }
}
