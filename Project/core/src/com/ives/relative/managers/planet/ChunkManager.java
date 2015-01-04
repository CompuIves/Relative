package com.ives.relative.managers.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.entities.components.planet.ChunkC;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager sorts every chunk and creates chunks when appropriate.
 */
@Wire
public class ChunkManager extends Manager {
    private static final int chunkSize = 128;
    protected PlanetManager planetManager;
    protected UuidEntityManager uuidEntityManager;
    protected ComponentMapper<ChunkC> mChunkC;

    public ChunkManager() {
    }

    /**
     * Get the tile from a chunk
     *
     * @param x
     * @param y
     * @param planet
     * @return
     */
    public Entity getTile(float x, float y, String planet) {
        Chunk chunk = getChunk(x, planet);
        UUID uuid = chunk.getTile(x, y);
        if (uuid != null) {
            return uuidEntityManager.getEntity(uuid);
        } else {
            return null;
        }
    }

    public void addTile(float x, float y, String planet, Entity tile) {
        Chunk chunk = getChunk(x, planet);
        chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
    }

    /**
     * Simple equation to check which chunk to get
     *
     * @param x
     * @return
     */
    public int getChunkIndex(float x) {
        return (int) x / chunkSize;
    }

    /**
     * Get all the chunks of the planet
     *
     * @param planet Planet of chunk
     * @return All the chunks
     */
    public Map<Integer, Chunk> getChunks(String planet) {
        Entity ePlanet = planetManager.getPlanet(planet);
        ChunkC chunkC = mChunkC.get(ePlanet);
        return chunkC.chunks;
    }

    /**
     * Get the chunk of the given x
     *
     * @param x      the coord of the chunk
     * @param planet the planet the chunk is on
     * @return the chunk
     */
    public Chunk getChunk(float x, String planet) {
        int index = getChunkIndex(x);
        Map<Integer, Chunk> chunks = getChunks(planet);

        if (chunks.containsKey(index)) {
            return chunks.get(index);
        } else {
            Chunk chunk = new Chunk(index * chunkSize, index * chunkSize + chunkSize, planet);
            chunks.put(index, chunk);
            return chunk;
        }
    }
}
