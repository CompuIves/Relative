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
    public static final int CHUNK_RADIUS = 3;
    public static final int SMALLEST_CHUNK_SIZE = 16;

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
     * Returns all chunks in a chunkradius around the chunk using neighbours, beautiful image to illustrate: <p></p>
     * <p/>
     * ######### <br></br>
     * ######### <br></br>
     * ####*#### <br></br>
     * ######### <br></br>
     * ######### <br></br>
     * * is paramater
     * <p></p>
     * Which will return this with chunkRadius 1: <p></p>
     * ######### <br></br>
     * ###***### <br></br>
     * ###*#*### <br></br>
     * ###***### <br></br>
     * ######### <br></br>
     * <p/>
     * <p></p>
     * This method does it a little bit different, but the result is the same.
     *
     * @param chunk       Initial chunk
     * @param chunkRadius Amount of iterations to process
     * @return the chunks excluding the chunk given as parameter
     */
    public Array<Chunk> getChunksAroundChunkg(Chunk chunk, int chunkRadius) {
        Array<Chunk> chunks = new Array<Chunk>(chunkRadius * chunkRadius);

        Vector2 rightVector = new Vector2(1, 0);
        Vector2 leftVector = new Vector2(-1, 0);
        Vector2 downVector = new Vector2(0, -1);
        Vector2 upVector = new Vector2(0, 1);

        //Get every chunks left and right
        chunks.addAll(getNeighbourChunks(chunk, chunkRadius, rightVector));
        chunks.addAll(getNeighbourChunks(chunk, chunkRadius, leftVector));

        int chunkAmount = chunks.size;
        //Get chunks down and up of the chunk
        for (int i = 0; i < chunkAmount; i++) {
            Chunk nChunk = chunks.get(i);
            chunks.addAll(getNeighbourChunks(nChunk, chunkRadius, downVector));
            chunks.addAll(getNeighbourChunks(nChunk, chunkRadius, upVector));
        }

        if (chunk.universeBody.hasChildren())
            loadChildrenAroundChunk(chunk, chunkRadius);
        return chunks;
    }

    public Array<Chunk> getChunksAroundChunk(Chunk chunk, int chunkRadius) {
        Array<Chunk> chunks = new Array<Chunk>(chunkRadius * chunkRadius);
        int startX = chunk.x - chunkRadius * SMALLEST_CHUNK_SIZE;
        int endX = chunk.x + chunkRadius * SMALLEST_CHUNK_SIZE;
        int startY = chunk.y - chunkRadius * SMALLEST_CHUNK_SIZE;
        int endY = chunk.y + chunkRadius * SMALLEST_CHUNK_SIZE;

        Vector2 v = new Vector2();
        for (int x = startX; x < endX; x += SMALLEST_CHUNK_SIZE) {
            System.out.println(x);
            for (int y = startY; y < endY; y += SMALLEST_CHUNK_SIZE) {
                Chunk c = getTopChunk(chunk.universeBody, v.set(x, y));
                if (c != null) {
                    if (c.edge && c.universeBody != chunk.universeBody) {
                        //Load parent too, this is an edge case
                        Chunk pChunk = getChunk(chunk.universeBody, v.set(x, y));
                        if (pChunk != null)
                            chunks.add(pChunk);
                    }
                    System.out.println("STARTED LOADING WITH X: " + c.x);
                    chunks.add(c);
                }
            }
        }

        return chunks;
    }

    /**
     * Loads all the children around the chunk
     *
     * @param chunk
     * @param chunkRadius
     */
    private void loadChildrenAroundChunk(Chunk chunk, int chunkRadius) {
        Vector2 pos = new Vector2(chunk.x, chunk.y);
        pos.x -= SMALLEST_CHUNK_SIZE * chunkRadius / 2 - 1;
        pos.y -= SMALLEST_CHUNK_SIZE * chunkRadius / 2 - 1;

        Vector2 v = new Vector2();
        for (int x = (int) pos.x; x < pos.x + chunkRadius * SMALLEST_CHUNK_SIZE; x++) {
            for (int y = (int) pos.y; y < pos.y + chunkRadius * SMALLEST_CHUNK_SIZE; y++) {
                Chunk nChunk = getChildChunk(chunk.universeBody, v.set(x, y));
                if (nChunk != null) {
                    loadChunk(nChunk);
                }
            }
        }
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
        //TODO why aren't chunks loading perfectly? Will have to fix this.
        MathUtils.clamp(pos.x, -universeBody.width / 2, universeBody.width / 2);
        MathUtils.clamp(pos.y, -universeBody.height / 2, universeBody.height / 2);

        if (universeBody.chunks.containsKey(pos)) {
            return universeBody.chunks.get(pos);
        } else {
            return createChunk(universeBody, (int) pos.x, (int) pos.y);
        }
    }

    /**
     * Gets the neighbour chunk
     *
     * @param chunk chunk to search around
     * @param dir   direction of the chunk, which means a vector without magnitude: (1,0) is right - (-1,0) is left - (0,1) is up etc. etc.
     * @return The neighbour chunk, null if there is no chunk found at pos (for example when neighbour is outside of UniverseBody).
     */
    private Chunk getNeighbourChunk(Chunk chunk, Vector2 dir) {
        int x = (int) (chunk.getX() + chunk.width * dir.x);
        int y = (int) (chunk.getY() + chunk.height * dir.y);
        return getChunk(chunk.universeBody, new Vector2(x, y));
    }

    /**
     * Gets a chunk which is distance chunks in direction, for example.
     * <p></p>
     * ####*####<br></br>
     * * is chunk given, distance is for example 3, direction is left <p></p>
     * <p/>
     * #*#######<br></br>
     * * is returned chunk
     *
     * @param chunk
     * @param distance
     * @param dir
     * @return
     */
    private Chunk getChunkInDirection(Chunk chunk, int distance, Vector2 dir) {
        Array<Chunk> chunks = getNeighbourChunks(chunk, distance, dir);
        return chunks.get(chunks.size - 1);
    }

    /**
     * Gets chunks in a specified direction and distance using {@link #getNeighbourChunk(Chunk, Vector2)}
     *
     * @param chunk
     * @param distance
     * @param dir      direction vector
     * @return
     */
    private Array<Chunk> getNeighbourChunks(Chunk chunk, int distance, Vector2 dir) {
        Array<Chunk> chunks = new Array<Chunk>(distance);
        Chunk startChunk = chunk;
        for (int i = 0; i < distance; i++) {
            Chunk iChunk = getNeighbourChunk(startChunk, dir);
            if (iChunk == null)
                break;

            chunks.add(iChunk);
            startChunk = iChunk;
        }
        return chunks;
    }

    /**
     * Gets the chunk of the lowest (childest) child of the universebody at that position. The pos given will also
     * be converted to the child coordinate system.
     * @param universeBody universebody to start searching
     * @param pos POSITION!
     * @return null if there was no child there, the chunk if there was indeed a child.
     */
    public Chunk getTopChunk(UniverseBody universeBody, Vector2 pos) {
        UniverseBody ub = universeBody.getBottomUniverseBody(pos, false);
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

    /**
     * Add an entity to a chunk
     * @param e
     */
    public void addEntityToChunk(Entity e) {
        Position position = mPosition.get(e);
        UniverseBody universeBody = position.universeBody;

        //Chunk coordinate system doesn't get transformed
        Vector2 pos = new Vector2(position.x, position.y);
        Chunk chunk = getTopChunk(universeBody, pos);
        chunk.addEntity(uuidEntityManager.getUuid(e));
        position.chunk = chunk;

        Gdx.app.debug("ChunkManager", "Entity " + e.toString() + " added to chunk " + chunk.toString());

        if (authorityManager.isEntityTemporaryAuthorized(e)) {
            for (Chunk lChunk : getChunksAroundChunk(chunk, CHUNK_RADIUS)) {
                loadChunk(lChunk);
            }
        }

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
     * Loads the chunk
     * @param chunk
     */
    public void loadChunk(Chunk chunk) {
        if (!chunkLoader.loadedChunks.contains(chunk, false)) {
            chunkLoader.requestChunk(chunk);
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
        int newPos = 0;
        int newSize = 0;
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
            Position p = mPosition.get(e);

            if (RelativeMath.fastfloor(p.px / p.universeBody.chunkSize) != RelativeMath.fastfloor(p.x / p.universeBody.chunkSize) ||
                    RelativeMath.fastfloor(p.py / p.universeBody.chunkSize) != RelativeMath.fastfloor(p.y / p.universeBody.chunkSize)) {
                removeEntityFromChunk(e);
                addEntityToChunk(e);
            }
        }
    }
}
