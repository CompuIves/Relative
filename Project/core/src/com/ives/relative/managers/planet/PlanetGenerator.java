package com.ives.relative.managers.planet;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;

/**
 * Created by Ives on 4/1/2015.
 */
@Wire
public class PlanetGenerator extends Manager {
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public void generateTerrain(Chunk chunk) {
        int startX = chunk.startX;
        int endX = chunk.endX;
        String planet = chunk.planet;

        for (int x = startX; x < endX; x++) {
            for (int y = 2; y < 10; y++) {
                Entity tile = tileManager.createTile(planet, x, y, 0, "dirt", false);
                chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
            }
        }

        for (int x = startX; x < endX; x++) {
            for (int y = 0; y < 2; y++) {
                Entity tile = tileManager.createTile(planet, x, y, 0, "bedrock", false);
                chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
            }
        }

        chunk.addTile(startX, 10, uuidEntityManager.getUuid(tileManager.createTile(planet, startX, 10, 0, "bedrock", false)));
        chunk.addTile(endX, 10, uuidEntityManager.getUuid(tileManager.createTile(planet, endX, 10, 0, "bedrock", false)));
    }
}
