package com.ives.relative.universe.chunks.builders;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * Creates a square planet
 */
public class SquarePlanet extends ChunkBuilder {
    Vector2 originalGravity;

    public SquarePlanet(Space space, TileManager tileManager, UuidEntityManager uuidEntityManager, Vector2 originalGravity) {
        super(space, tileManager, uuidEntityManager);

        this.originalGravity = originalGravity;
    }

    @Override
    public Chunk buildChunk(int x, int y, int width, int height) {
        Chunk chunk = new Chunk(space, x, y, width, height, 0);
        Pixmap bg = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        bg.setColor(new Color(0.5f, 0.9f, 1f, 1f));
        bg.fill();
        chunk.bgColor = bg;
        return chunk;
    }

    @Override
    public void generateTerrain(Chunk chunk) {
        int startX = chunk.x;
        int startY = chunk.y;
        int endX = startX + chunk.width;
        int endY = startY + chunk.height;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (Math.abs(x) <= space.width / 3 && Math.abs(y) <= space.height / 3) {
                    Entity tile;
                    if (y + 1 > space.height / 3) {
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
