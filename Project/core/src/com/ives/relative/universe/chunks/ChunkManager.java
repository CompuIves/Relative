package com.ives.relative.universe.chunks;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.creation.EntityDeletionEvent;
import com.ives.relative.entities.events.position.JoinChunkEvent;
import com.ives.relative.entities.events.position.LeaveChunkEvent;
import com.ives.relative.entities.events.position.MovementEvent;
import com.ives.relative.entities.events.position.NearUniverseBorderEvent;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.chunks.chunkloaders.ChunkLoader;
import com.ives.relative.utils.RelativeMath;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager manages every chunk ({@link com.ives.relative.universe.chunks.Chunk}). Chunks are stored in a {@link Space},
 * this manager serves as a central manager for chunk related algorithms and loading.
 */
@Wire
public class ChunkManager extends Manager implements EntityEventObserver {
    public static final int CHUNK_RADIUS = 40;
    /**
     * use only synchronous!
     */
    private static Vector2 tempVec1 = new Vector2();
    private static Vector2 tempVec2 = new Vector2();
    private static Vector3 tempVec3 = new Vector3();
    private final ChunkLoader chunkLoader;
    protected ComponentMapper<Position> mPosition;
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;
    protected AuthorityManager authorityManager;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    /**
     * Converts coordinate to chunk coordinate (or not when the coords are out of bound)
     * @param space
     * @param pos
     */
    public void convertToChunkCoord(Space space, Vector2 pos) {
        pos.x = RelativeMath.fastfloor(pos.x / space.chunkSize) * space.chunkSize;
        pos.y = RelativeMath.fastfloor(pos.y / space.chunkSize) * space.chunkSize;
        if(!space.infinite) {
            pos.x = MathUtils.clamp(pos.x, -space.width / 2, space.width / 2);
            pos.y = MathUtils.clamp(pos.y, -space.height / 2, space.height / 2);
        }
    }

    /**
     * Creates a chunk at specified indexes
     * @param x index of first chunk
     * @param y index of second chunk
     * @return
     */
    public Chunk createChunk(Space space, int x, int y) {
        Chunk chunk = null;
        int width = space.chunkSize, height = space.chunkSize;
        boolean isEdge = false;
        if(!space.infinite) {
            if (isEdge(x, space.width, space.chunkSize)) {
                Vector2 horizontalPos = sliceEdge(x, space.width, space.chunkSize);
                x = (int) horizontalPos.x;
                width = (int) horizontalPos.y;
                isEdge = true;
            }

            if (isEdge(y, space.height, space.chunkSize)) {
                Vector2 verticalPos = sliceEdge(y, space.height, space.chunkSize);
                y = (int) verticalPos.x;
                height = (int) verticalPos.y;
                isEdge = true;
            }
        }

        if (width > 0 && height > 0) {
            chunk = space.chunkBuilder.buildChunk(x, y, width, height, isEdge);
            space.chunks.put(new Vector2(x, y), chunk);
        }
        return chunk;
    }

    /**
     * Gets the chunk at the x coordinate and y coordinate.
     *
     * @param space the body the chunk is in
     * @param pos position
     * @return the chunk, null if the chunk is out of bounds (not in this universebody).
     */
    public Chunk getChunk(Space space, Vector2 pos) {
        convertToChunkCoord(space, pos);
        if (space.chunks.containsKey(pos)) {
            return space.chunks.get(pos);
        } else {
            return createChunk(space, (int) pos.x, (int) pos.y);
        }
    }

    /**
     * Add an entity to chunk
     */
    public void addEntityToChunk(Entity e) {
        Position p = mPosition.get(e);
        Chunk chunk = getChunk(p.space, new Vector2(p.x, p.y));
        addEntityToChunk(e, chunk);
    }

    /**
     * Add an entity to a chunk (use this method is used when you already know which chunk)
     */
    public void addEntityToChunk(Entity e, Chunk chunk) {
        Position p = mPosition.get(e);
        chunk.addEntity(uuidEntityManager.getUuid(e));
        p.chunk = chunk;

        Gdx.app.debug("ChunkManager", "Entity " + e.toString() + " added to chunk " + chunk.toString());
        JoinChunkEvent event = (JoinChunkEvent) eventManager.getEvent(JoinChunkEvent.class, e);
        event.chunk = p.chunk;
        eventManager.notifyEvent(event);
    }

    /**
     * Removes an entity from a chunk
     */
    public void removeEntityFromChunk(Entity e) {
        if (mPosition.has(e)) {
            Position position = mPosition.get(e);
            if (position.chunk != null) {
                position.chunk.removeEntity(uuidEntityManager.getUuid(e));

                LeaveChunkEvent event = (LeaveChunkEvent) eventManager.getEvent(LeaveChunkEvent.class, e);
                event.chunk = position.chunk;
                eventManager.notifyEvent(event);
                position.chunk = null;
            }
        }
    }

    /**
     * Returns chunks around a chunk, this includes chunks of children and parents.
     * @param centerChunk the center of the search
     * @param radius radius in blocks
     * @return Array of chunks around the given chunks, including the centerchunk
     */
    private Array<Chunk> getChunksAroundChunk(Chunk centerChunk, int radius) {
        Array<Chunk> chunks = new Array<Chunk>(radius / centerChunk.space.chunkSize * radius / centerChunk.space.chunkSize);
        int startX = centerChunk.x - radius;
        int endX = centerChunk.x + radius;
        int startY = centerChunk.y - radius;
        int endY = centerChunk.y + radius;

        Vector2 v = new Vector2();
        for (int x = startX; x < endX; x += 1) {
            for (int y = startY; y < endY; y += 1) {
                Chunk c = getChunk(centerChunk.space, v.set(x, y));
                if (c != null) {
                    if (!chunks.contains(c, false)) {
                        chunks.add(c);
                    }
                }
            }
        }

        return chunks;
    }

    /**
     * Gets and loads every chunk around an entity
     *
     * @param chunk chunk the entity is in
     * @param e     entity
     */
    private void loadChunksAroundEntity(Chunk chunk, Entity e) {
        Array<Chunk> chunks = getChunksAroundChunk(chunk, CHUNK_RADIUS);
        Array<Chunk> loadedChunks = new Array<Chunk>();
        loadedChunks.addAll(chunkLoader.loadedChunks);

        for (Chunk loadedChunk : loadedChunks) {
            loadedChunk.removeLoadedByPlayer(e.getUuid());
        }
        loadedChunks.removeAll(chunks, false);

        for (Chunk newChunk : chunks) {
            //Add the 'player' to the chunk as loaded
            newChunk.addLoadedByPlayer(e.getUuid());
            preLoadChunk(newChunk);
        }

        for (Chunk oldChunk : loadedChunks) {
            //Removes the 'player' from every chunk as loader
            unloadChunk(oldChunk);
        }
    }

    /**
     * Preloads the chunk, this means loading the chunk from disc or requesting the chunk over the network
     */
    public void preLoadChunk(Chunk chunk) {
        if (!chunkLoader.loadedChunks.contains(chunk, false) && !chunkLoader.requestedChunks.contains(chunk, false)) {
            chunkLoader.preLoadChunk(chunk);
        }
    }

    /**
     * When the chunk is requested and the info is put into the chunk the chunk can be loaded. Chunk has to be
     * preloaded first ({@link #preLoadChunk(Chunk)})
     */
    public void loadChunk(Chunk chunk) {
        if (chunkLoader.requestedChunks.contains(chunk, false)) {
            chunkLoader.loadChunk(chunk);
        }
    }

    /**
     * Unloads the chunk
     */
    public void unloadChunk(Chunk chunk) {
        if (chunkLoader.loadedChunks.contains(chunk, false))
            chunkLoader.unloadChunk(chunk, uuidEntityManager);
    }

    /**
     * Checks if the given position (x or y) is out of bounds the Space when the chunkSize is added.
     *
     * @param pos       x or y, depends if you want to know the horizontal edge or vertical edge.
     * @param uBodSize  size of universebody
     * @param chunkSize size of chunks
     * @return if the chunk at that position is an edge
     */
    private boolean isEdge(int pos, int uBodSize, int chunkSize) {
        if (pos < 0) {
            return pos < -uBodSize / 2;
        } else {
            return Math.abs(pos) + chunkSize > Math.abs(uBodSize) / 2;
        }
    }

    /**
     * Slices a chunk to fit the chunk in the uBody
     *
     * @param pos       position in x or y
     * @param uBodSize  size of the universe
     * @param chunkSize initial chunk size
     * @return A Vector with values (newPos, newSize)
     */
    private Vector2 sliceEdge(int pos, int uBodSize, int chunkSize) {
        int newPos;
        int newSize;
        if (pos > 0) {
            newPos = pos;
            newSize = uBodSize / 2 - pos;
        } else {
            newPos = -uBodSize / 2;
            newSize = uBodSize / 2 + pos + chunkSize;
        }
        return new Vector2(newPos, newSize);
    }

    /**
     * @return all loaded chunks
     */
    public Array<Chunk> getLoadedChunks() {
        return chunkLoader.loadedChunks;
    }

    /**
     * Handles loading chunks and responding on entity removals
     * @param event
     */
    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof MovementEvent) {
            Entity e = event.entity;
            Position p = ((MovementEvent) event).position;

            tempVec1.set(p.px, p.py);
            tempVec2.set(p.x, p.y);

            Chunk nChunk = getChunk(p.space, tempVec1);
            if (!nChunk.equals(p.chunk)) {
                removeEntityFromChunk(e);
                addEntityToChunk(e, nChunk);

                if (authorityManager.isEntityTemporaryAuthorized(e))
                    loadChunksAroundEntity(p.chunk, e);
            }

        } else if (event instanceof EntityDeletionEvent) {
            removeEntityFromChunk(event.entity);
        } else if (event instanceof NearUniverseBorderEvent) {
            Position p = mPosition.get(event.entity);
            loadChunksAroundEntity(p.chunk, event.entity);
        }
    }
}