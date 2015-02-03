package com.ives.relative.universe.chunks;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.*;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.UniverseBody;
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
    public static final int CHUNK_RADIUS = 40;
    static Vector2 newVec = new Vector2();
    public final ChunkLoader chunkLoader;
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
     * @param universeBody
     * @param pos
     */
    public void convertToChunkCoord(UniverseBody universeBody, Vector2 pos) {
        //If the position is in in bounds for clamping
        if(pos.x > -universeBody.width / 2 - universeBody.chunkSize && pos.y > -universeBody.height / 2 - universeBody.chunkSize) {
            //Get the chunk coordinate
            pos.x = RelativeMath.fastfloor(pos.x / universeBody.chunkSize) * universeBody.chunkSize;
            pos.y = RelativeMath.fastfloor(pos.y / universeBody.chunkSize) * universeBody.chunkSize;


            MathUtils.clamp(pos.x, -universeBody.width / 2, universeBody.width / 2);
            MathUtils.clamp(pos.y, -universeBody.height / 2, universeBody.height / 2);
        }
    }

    /**
     * Creates a chunk at specified indexes
     * @param x index of first chunk
     * @param y index of second chunk
     * @return
     */
    public Chunk createChunk(UniverseBody universeBody, int x, int y) {
        Chunk chunk = null;
        int width = universeBody.chunkSize, height = universeBody.chunkSize;
        boolean isEdge = false;

        if (isEdge(x, universeBody.width, universeBody.chunkSize)) {
            Vector2 horizontalPos = sliceEdge(x, universeBody.width, universeBody.chunkSize);
            x = (int) horizontalPos.x;
            width = (int) horizontalPos.y;
            isEdge = true;
        }

        if (isEdge(y, universeBody.height, universeBody.chunkSize)) {
            Vector2 verticalPos = sliceEdge(y, universeBody.height, universeBody.chunkSize);
            y = (int) verticalPos.x;
            height = (int) verticalPos.y;
            isEdge = true;
        }

        if (width > 0 && height > 0) {
            chunk = universeBody.chunkBuilder.buildChunk(x, y, width, height, isEdge);
            universeBody.chunks.put(new Vector2(x, y), chunk);
        }
        return chunk;
    }

    /**
     * Gets the chunk at the x coordinate and y coordinate.
     *
     * @param universeBody the body the chunk is in
     * @param pos
     * @return the chunk, null if the chunk is out of bounds (not in this universebody).
     */
    public Chunk getChunk(UniverseBody universeBody, Vector2 pos) {
        convertToChunkCoord(universeBody, pos);

        if (universeBody.chunks.containsKey(pos)) {
            return universeBody.chunks.get(pos);
        } else {
            return createChunk(universeBody, (int) pos.x, (int) pos.y);
        }
    }

    /**
     * Gets the chunk of the lowest (childest) child of the universebody at that position. The pos given will also
     * be converted to the child coordinate system.
     * @param universeBody universebody to start searching
     * @param pos POSITION!
     * @return null if there was no child there, the chunk if there was indeed a child.
     */
    public Chunk getTopChunk(UniverseBody universeBody, Vector2 pos) {
        UniverseBody ub = universeBody.getTopUniverseBody(pos, false);
        return getChunk(ub, pos);
    }

    /**
     * Gets the chunk of the parent at that given position
     *
     * @param universeBody
     * @param pos
     * @return
     */
    public Chunk getParentChunk(UniverseBody universeBody, Vector2 pos) {
        UniverseBody parent = universeBody.getParent();
        universeBody.transformVector(pos);
        return getChunk(parent, pos);
    }

    /**
     * Gets the chunk of the lowest child of the universebody, if there is no child, return null
     *
     * @param universeBody parent
     * @param pos position relative to parent
     * @return chunk
     */
    public Chunk getChildChunk(UniverseBody universeBody, Vector2 pos) {
        if (universeBody.hasChildren()) {
            if (universeBody.getChild(pos) != null) {
                UniverseBody child = universeBody.getBottomChild(pos, false);
                return getChunk(child, pos);
            }
        }

        return null;
    }

    public void addEntityToChunk(Entity e) {
        Position p = mPosition.get(e);
        Chunk chunk = getTopChunk(p.universeBody, new Vector2(p.x, p.y));
        addEntityToChunk(e, chunk);
    }

    /**
     * Add an entity to a chunk
     * @param e
     */
    public void addEntityToChunk(Entity e, Chunk chunk) {
        Position p = mPosition.get(e);
        chunk.addEntity(uuidEntityManager.getUuid(e));
        p.chunk = chunk;

        Gdx.app.debug("ChunkManager", "Entity " + e.toString() + " added to chunk " + chunk.toString());
        eventManager.notifyEvent(new JoinChunkEvent(e, chunk));
    }

    /**
     * Removes an entity from a chunk
     * @param e
     */
    public void removeEntityFromChunk(Entity e) {
        Position position = mPosition.get(e);

        position.chunk.removeEntity(uuidEntityManager.getUuid(e));

        eventManager.notifyEvent(new LeaveChunkEvent(e, position.chunk));
        position.chunk = null;
    }

    /**
     * Loads chunks around a chunk, this includes chunks of children and parents.
     * @param centerChunk
     * @param radius radius in blocks
     * @return
     */
    private Array<Chunk> getChunksAroundChunk(Chunk centerChunk, int radius) {
        Array<Chunk> chunks = new Array<Chunk>(radius / centerChunk.universeBody.chunkSize * radius / centerChunk.universeBody.chunkSize);
        int startX = centerChunk.x - radius;
        int endX = centerChunk.x + radius;
        int startY = centerChunk.y - radius;
        int endY = centerChunk.y + radius;

        Vector2 v = new Vector2();
        for (int x = startX; x < endX; x += 2) {
            for (int y = startY; y < endY; y += 2) {
                Chunk c = getTopChunk(centerChunk.universeBody, v.set(x, y));
                if (c != null) {
                    if (c.edge && c.universeBody != centerChunk.universeBody) {
                        //Load parent too, this is an edge case
                        Chunk pChunk = getChunk(centerChunk.universeBody, v.set(x, y));
                        if (pChunk != null) {
                            if (!chunks.contains(pChunk, false))
                                chunks.add(pChunk);
                        }
                    }
                    if (!chunks.contains(c, false))
                        chunks.add(c);
                }
            }
        }

        return chunks;
    }

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
            loadChunk(newChunk);
        }

        for (Chunk oldChunk : loadedChunks) {
            //Removes the 'player' from every chunk as loader
            unloadChunk(oldChunk);
        }
    }

    /**
     * Loads the chunk
     * @param chunk
     */
    public void loadChunk(Chunk chunk) {
        if (!chunkLoader.loadedChunks.contains(chunk, false) && !chunkLoader.requestedChunks.contains(chunk, false)) {
            chunkLoader.preLoadChunk(chunk);
        }
    }

    /**
     * Unloads the chunk
     * @param chunk
     */
    public void unloadChunk(Chunk chunk) {
        if (chunkLoader.loadedChunks.contains(chunk, false))
            chunkLoader.unloadChunk(chunk, uuidEntityManager);
    }

    /**
     * Checks if the given position (x or y) is out of bounds the UniverseBody when the chunkSize is added.
     *
     * @param pos       x or y, depends if you want to know the horizontal edge or vertical edge.
     * @param uBodSize
     * @param chunkSize
     * @return
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

    public Array<Chunk> getLoadedChunks() {
        return chunkLoader.loadedChunks;
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof MovementEvent) {
            Entity e = event.entity;
            Position p = ((MovementEvent) event).position;
            newVec.set(p.x, p.y);

            Chunk chunk = getTopChunk(p.universeBody, newVec);
            if (chunk != p.chunk) {
                System.out.println(chunk);
                System.out.println(p.chunk);
                removeEntityFromChunk(e);
                addEntityToChunk(e, chunk);
            }

            if (authorityManager.isEntityTemporaryAuthorized(e)) {
                //TODO could have optimization?
                loadChunksAroundEntity(p.chunk, e);
            }
        }
    }
}
