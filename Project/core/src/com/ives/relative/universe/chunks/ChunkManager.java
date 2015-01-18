package com.ives.relative.universe.chunks;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.Galaxy;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.chunkloaders.ChunkLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager manages every chunk. It keeps track of the registered chunks ({@link com.ives.relative.universe.chunks.ChunkManager#chunkMap}) and handles loading and unloading
 * chunks.
 */
@Wire
public class ChunkManager extends Manager {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_LOAD = 5;
    private final Map<Vector2, Chunk> chunkMap;
    protected ChunkLoader chunkLoader;
    protected UniverseManager universeManager;
    protected UuidEntityManager uuidEntityManager;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
        chunkMap = new HashMap<Vector2, Chunk>();
    }

    private UniverseBody findHighestUniverseBody(int x, int y) {
        Galaxy galaxy = universeManager.universe.getGalaxy(x, y);
        return galaxy.getChild(x, y);
    }

    private Chunk createChunk(int x, int y) {
        Chunk chunk;
        UniverseBody universeBody = findHighestUniverseBody(x, y);
        chunk = universeBody.chunkBuilder.buildChunk(x, y);
        return chunk;
    }

    public Chunk getChunk(int x, int y) {
        Vector2 pos = new Vector2(x, y);
        if (chunkMap.containsKey(pos)) {
            return chunkMap.get(pos);
        } else {
            return createChunk(x, y);
        }
    }

    public void loadChunk(Chunk chunk) {
        chunkLoader.loadChunk(chunk);
    }

    public void unLoadChunk(Chunk chunk) {
        chunkLoader.unLoadChunk(chunk, uuidEntityManager);
    }
}
