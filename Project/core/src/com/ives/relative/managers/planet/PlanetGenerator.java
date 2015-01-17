package com.ives.relative.managers.planet;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.managers.planet.chunks.Chunk;
import com.ives.relative.managers.planet.chunks.ChunkManager;

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
        String planet = chunk.planet;


        if (planet != null) {
            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    Entity tile = tileManager.createTile(planet, x, y, 0, "dirt", false);
                    chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
                }
            }

            chunk.addTile(startX, startY, uuidEntityManager.getUuid(tileManager.createTile(planet, startX, startY, 0, "bedrock", false)));
            chunk.addTile(startX, endY - 1, uuidEntityManager.getUuid(tileManager.createTile(planet, startX, endY - 1, 0, "bedrock", false)));
            chunk.addTile(endX, startY, uuidEntityManager.getUuid(tileManager.createTile(planet, endX, startY, 0, "bedrock", false)));
            chunk.addTile(endX, endY - 1, uuidEntityManager.getUuid(tileManager.createTile(planet, endX, endY - 1, 0, "bedrock", false)));
        }
    }
}
