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
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.chunkloaders.ChunkLoader;
import com.ives.relative.utils.RelativeMath;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager manages every chunk ({@link com.ives.relative.universe.chunks.Chunk}). Chunks are stored in a {@link com.ives.relative.universe.UniverseBody},
 * this manager serves as a central manager for chunk related algorithms and loading.
 */
@Wire
public class ChunkManager extends Manager implements EntityEventObserver {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_LOAD = 5;
    public final ChunkLoader chunkLoader;
    protected ComponentMapper<Position> mPosition;
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;
    protected AuthorityManager authorityManager;
    protected UniverseSystem universeSystem;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    public Array<Chunk> getChunksAroundChunk(Chunk chunk) {
        Array<Chunk> chunks = new Array<Chunk>(CHUNK_LOAD * CHUNK_LOAD);
        UniverseBody universeBody = chunk.universeBody;

        int deviation = (CHUNK_LOAD - 1) / 2;
        int posDeviation = deviation * 16;

        //Temporary vector
        Vector2 v = new Vector2();
        for (int x = chunk.x - posDeviation; x <= chunk.x + posDeviation; x += CHUNK_SIZE) {
            for (int y = chunk.y - posDeviation; y <= chunk.y + posDeviation; y += CHUNK_SIZE) {
                chunks.add(getChunk(universeBody, v.set(x, y)));
                Chunk childChunk = getTopChunk(universeBody, v);
                if(childChunk != null)
                    chunks.add(childChunk);
            }
        }
        return chunks;
    }

    /**
     * Gets the chunk at the x coordinate and y coordinate.
     *
     * @param universeBody the body the chunk is in
     * @param pos
     * @return the chunk, null if the chunk is out of bounds (not in this universebody).
     */
    public Chunk getChunk(UniverseBody universeBody, Vector2 pos) {
        //Get the chunk coordinate
        pos.x = RelativeMath.fastfloor(pos.x/universeBody.chunkSize) * universeBody.chunkSize;
        pos.y = RelativeMath.fastfloor(pos.y/universeBody.chunkSize) * universeBody.chunkSize;
        if (universeBody.chunks.containsKey(pos)) {
            return universeBody.chunks.get(pos);
        } else {
            return createChunk(universeBody, (int) pos.x, (int) pos.y);
        }
    }

    /**
     * Gets the chunk of the lowest child of the universebody at that position. This is relative to the
     * child.
     * @param universeBody parent universebody
     * @param pos POSITION!
     * @return null if there was no child there, the chunk if there was indeed a child.
     */
    public Chunk getTopChunk(UniverseBody universeBody, Vector2 pos) {
        UniverseBody child = universeBody.getLowestChild(pos, false);
        return getChunk(child, pos);
    }

    public void addEntityToChunk(Entity e) {
        Position position = mPosition.get(e);
        //If the entity already has a position it just gets the universebody of its position, otherwise FIND IT!
        UniverseBody universeBody = position.universeBody;

        //Chunk coordinate system doesn't get transformed
        Vector2 pos = new Vector2(position.x, position.y);
        Chunk chunk = getTopChunk(universeBody, pos);
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


    /**
     * Creates a chunk at specified indexes
     * @param x index of first chunk
     * @param y index of second chunk
     * @return
     */
    public Chunk createChunk(UniverseBody universeBody, int x, int y) {
        Vector2 chunkLoc = new Vector2(x, y);
        if (universeBody.isInBody(chunkLoc)) {
            Chunk chunk;
            chunk = universeBody.chunkBuilder.buildChunk(x, y);
            universeBody.chunks.put(new Vector2(x, y), chunk);
            return chunk;
        } else
            return null;
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
