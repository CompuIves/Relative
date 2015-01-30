package com.ives.relative.universe.chunks.chunkloaders;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 7/1/2015.
 */
public abstract class ChunkLoader {
    public Array<Chunk> loadedChunks;
    private Map<String, Integer> tileLegend;

    public ChunkLoader() {
        loadedChunks = new Array<Chunk>();
        tileLegend = new HashMap<String, Integer>();
        loadTileLegend();
    }

    /**
     * This starts preparation of getting a chunk, like loading the file from a disk or sending a requestpacket.
     *
     * @param chunk
     */
    public abstract void requestChunk(Chunk chunk);

    abstract void loadTileLegend();

    public void loadChunk(Chunk chunk) {
        System.out.println("Loading chunk with: " + chunk.universeBody.chunkBuilder.getClass().getSimpleName());
        chunk.universeBody.chunkBuilder.generateTerrain(chunk);
        chunk.loaded = true;
        if (!loadedChunks.contains(chunk, false))
        loadedChunks.add(chunk);
    }

    public void unloadChunk(Chunk chunk, UuidEntityManager uuidEntityManager) {
        for (UUID tile : chunk.tiles.values()) {
            Entity eTile = uuidEntityManager.getEntity(tile);
            ComponentUtils.removeEntity(eTile);
        }

        for (Vector2 changedTile : chunk.changedTiles.keySet()) {
            UUID tile = chunk.getTile((int) changedTile.x, (int) changedTile.y);
            if (tile != null) {
                Entity eTile = uuidEntityManager.getEntity(tile);
                if (eTile != null) {
                    ComponentUtils.removeEntity(eTile);
                }
            }
        }

        for (UUID entityID : chunk.entities) {
            Entity entity = uuidEntityManager.getEntity(entityID);
            ComponentUtils.removeEntity(entity);
        }

        chunk.dispose();
        loadedChunks.removeValue(chunk, false);
    }
}
