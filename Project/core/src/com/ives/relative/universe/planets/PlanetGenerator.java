package com.ives.relative.universe.planets;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;

/**
 * Created by Ives on 4/1/2015.
 *
 * The official generator of a planet!
 */
@Wire
public class PlanetGenerator extends Manager {
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public void generateTerrain(Chunk chunk) {
        int chunkSize = ChunkManager.CHUNK_SIZE;

        int startX = chunk.x * chunkSize;
        int startY = chunk.y * chunkSize;
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
        chunk.addTile(startX, endY - 1, uuidEntityManager.getUuid(tileManager.createTile(startX, endY - 1, 0, "bedrock", false)));
        chunk.addTile(endX, startY, uuidEntityManager.getUuid(tileManager.createTile(endX, startY, 0, "bedrock", false)));
        chunk.addTile(endX, endY - 1, uuidEntityManager.getUuid(tileManager.createTile(endX, endY - 1, 0, "bedrock", false)));
    }
}
