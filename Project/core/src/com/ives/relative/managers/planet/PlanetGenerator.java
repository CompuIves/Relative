package com.ives.relative.managers.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.entities.components.planet.ChunkC;

/**
 * Created by Ives on 4/1/2015.
 *
 * The official generator of a planet!
 */
@Wire
public class PlanetGenerator extends Manager {
    protected TileManager tileManager;
    protected PlanetManager planetManager;
    protected UuidEntityManager uuidEntityManager;

    protected ComponentMapper<ChunkC> mChunkC;

    public void generateTerrain(Chunk chunk) {
        Entity ePlanet = planetManager.getPlanet(chunk.planet);
        int chunkSize = mChunkC.get(ePlanet).chunkSize;

        int startX = chunk.x * chunkSize;
        int startY = chunk.y * chunkSize;
        int endX = startX + chunkSize;
        int endY = startY + chunkSize;
        String planet = chunk.planet;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (y < 10) {
                    Entity tile = tileManager.createTile(planet, x, y, 0, "dirt", false);
                    chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
                }
            }
        }

        chunk.addTile(startX, startY, uuidEntityManager.getUuid(tileManager.createTile(planet, startX, startY, 0, "bedrock", false)));
        chunk.addTile(startX, endY, uuidEntityManager.getUuid(tileManager.createTile(planet, startX, endY, 0, "bedrock", false)));
        chunk.addTile(endX, startY, uuidEntityManager.getUuid(tileManager.createTile(planet, endX, startY, 0, "bedrock", false)));
        chunk.addTile(endX, endY, uuidEntityManager.getUuid(tileManager.createTile(planet, endX, endY, 0, "bedrock", false)));
    }
}
