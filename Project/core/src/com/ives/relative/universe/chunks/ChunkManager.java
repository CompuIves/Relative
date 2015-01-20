package com.ives.relative.universe.chunks;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.*;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.chunkloaders.ChunkLoader;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager manages every chunk. It keeps track of the registered chunks ({@link com.ives.relative.universe.chunks.ChunkManager#chunkMap}) and handles loading and unloading
 * chunks.
 */
@Wire
public class ChunkManager extends Manager implements EntityEventObserver {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_LOAD = 5;
    public final ChunkLoader chunkLoader;
    private final Map<Vector2, Chunk> chunkMap;
    protected ComponentMapper<Position> mPosition;
    protected UniverseManager universeManager;
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;
    protected AuthorityManager authorityManager;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
        chunkMap = new HashMap<Vector2, Chunk>();
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    private UniverseBody findHighestUniverseBody(int x, int y) {
        UniverseBody galaxy = universeManager.getGalaxy(x, y);
        if (galaxy != null) {
            return galaxy.getChild(x, y);
        } else {
            return null;
        }
    }

    private Chunk createChunk(int x, int y) {
        Chunk chunk;
        UniverseBody universeBody = findHighestUniverseBody(x, y);
        chunk = universeBody.chunkBuilder.buildChunk(x, y);
        chunkMap.put(new Vector2(x, y), chunk);
        return chunk;
    }

    public Array<Chunk> getChunksAroundChunk(Chunk chunk) {
        Array<Chunk> chunks = new Array<Chunk>(CHUNK_LOAD * CHUNK_LOAD);

        int deviation = (CHUNK_LOAD - 1) / 2;
        int posDeviation = deviation * 16;

        for (int x = chunk.x - posDeviation; x <= chunk.x + posDeviation; x += CHUNK_SIZE) {
            for (int y = chunk.y - posDeviation; y <= chunk.y + posDeviation; y += CHUNK_SIZE) {
                chunks.add(getChunk(x, y));
            }
        }
        return chunks;
    }

    public Chunk getChunk(float x, float y) {
        return getChunk(RelativeMath.fastfloor(x / CHUNK_SIZE) * CHUNK_SIZE, RelativeMath.fastfloor(y / CHUNK_SIZE) * CHUNK_SIZE);
    }

    public Chunk getChunk(int x, int y) {
        Vector2 pos = new Vector2(x, y);
        if (chunkMap.containsKey(pos)) {
            return chunkMap.get(pos);
        } else {
            return createChunk(x, y);
        }
    }

    public void addEntityToChunk(Entity e) {
        Position position = mPosition.get(e);
        Chunk chunk = getChunk(position.x, position.y);
        chunk.addEntity(uuidEntityManager.getUuid(e));
        position.chunk = chunk;

        if (authorityManager.isEntityTemporaryAuthorized(e)) {
            for (Chunk lChunk : getChunksAroundChunk(chunk)) {
                loadChunk(lChunk);
            }
        }

        eventManager.notifyEvent(new JoinChunkEvent(e, chunk));
    }

    public void removeEntityFromChunk(Entity e) {
        Position position = mPosition.get(e);
        position.chunk.removeEntity(uuidEntityManager.getUuid(e));

        eventManager.notifyEvent(new LeaveChunkEvent(e, position.chunk));
        position.chunk = null;
    }

    public void loadChunk(Chunk chunk) {
        if (!chunkLoader.loadedChunks.contains(chunk, false)) {
            chunkLoader.requestChunk(chunk);
        }
    }

    public void unloadChunk(Chunk chunk) {
        if (chunkLoader.loadedChunks.contains(chunk, false))
            chunkLoader.unloadChunk(chunk, uuidEntityManager);
    }

    public Array<Chunk> getLoadedChunks() {
        return chunkLoader.loadedChunks;
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof MovementEvent) {
            Entity e = event.entity;
            Position p = mPosition.get(e);

            if (RelativeMath.fastfloor(p.px / CHUNK_SIZE) != RelativeMath.fastfloor(p.x / CHUNK_SIZE) ||
                    RelativeMath.fastfloor(p.py / CHUNK_SIZE) != RelativeMath.fastfloor(p.y / CHUNK_SIZE)) {
                removeEntityFromChunk(e);
                addEntityToChunk(e);
            }
        }
    }
}
