package com.ives.relative.managers.planet.chunks;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.planet.ChunkC;
import com.ives.relative.entities.components.planet.Size;
import com.ives.relative.entities.events.*;
import com.ives.relative.entities.events.creation.NetworkedEntityCreationEvent;
import com.ives.relative.entities.events.creation.NetworkedEntityDeletionEvent;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.planet.PlanetGenerator;
import com.ives.relative.managers.planet.TileManager;
import com.ives.relative.managers.planet.chunks.chunkloaders.ChunkLoader;
import com.ives.relative.utils.ComponentUtils;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager sorts every chunk and creates chunks when appropriate.
 */
@Wire
public class ChunkManager extends Manager implements EntityEventObserver {
    public static final int CHUNK_LOAD = 5;
    public static final int CHUNK_SIZE = 16;

    public Map<Vector2, Chunk> registeredChunks;
    public Array<Chunk> loadedChunks;

    protected PlanetGenerator planetGenerator;
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;

    protected ComponentMapper<ChunkC> mChunkC;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Authority> mAuthority;
    protected ComponentMapper<Size> mSize;
    protected ComponentMapper<Name> mName;

    protected ChunkLoader chunkLoader;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
        loadedChunks = new Array<Chunk>();
        registeredChunks = new HashMap<Vector2, Chunk>();
    }

    @Override
    protected void initialize() {
        super.initialize();
        eventManager.addObserver(this);
    }

    public void generateChunkMap(Entity planet) {
        Name name = mName.get(planet);
        ChunkC chunkC = mChunkC.get(planet);
        Size size = mSize.get(planet);

        for (int i = 0; i < 4; i++) {
            PlanetSide side = new PlanetSide(name.internalName, size.width, size.height, i * 90, 0, -10);
            side.generateChunkMap();
            chunkC.sides.add(side);
            registeredChunks.putAll(side.chunks);
        }
    }

    /**
     *
     * @param x coordinate the chunk lies in
     * @param y coordinate the chunk lies in
     * @return
     */
    public Chunk getChunk(float x, float y) {
        int cx = RelativeMath.fastfloor(x / CHUNK_SIZE);
        int cy = RelativeMath.fastfloor(y / CHUNK_SIZE);
        return getChunk(cx, cy);
    }

    /**
     * Get the chunk of the given x
     *
     * @param x the x index of the chunk
     * @param y the y index of the chunk
     * @return the chunk
     */
    public Chunk getChunk(int x, int y) {
        Vector2 position = new Vector2(x, y);

        if (registeredChunks.containsKey(position)) {
            return registeredChunks.get(position);
        } else {
            //TODO fix gravity
            Chunk chunk = new Chunk(x, y, 0, 0, 0);
            registeredChunks.put(position, chunk);
            return chunk;
        }
    }

    public void loadAllChunks() {
        for (Chunk chunk : registeredChunks.values()) {
            loadChunk(chunk);
        }
    }


    /**
     * Loads the chunks around the player based on a radius of {@link ChunkManager#CHUNK_LOAD}.
     * There is a check built in if the chunk is already loaded, there is no need to check for it again.
     *
     * @param player player to be based around
     */
    public void loadChunksAroundEntity(Entity player) {
        for (Chunk chunk : getChunksSurroundingEntity(player)) {
            if (!chunk.loaded) {
                loadChunk(chunk);
            }
        }
    }

    /**
     * Returns the chunks surrounding the entity following a (square) radius of {@link ChunkManager#CHUNK_LOAD}
     *
     * @param player
     * @return array of the chunks surrounding the player
     */
    public Array<Chunk> getChunksSurroundingEntity(Entity player) {
        Array<Chunk> chunkSurrounding = new Array<Chunk>(CHUNK_LOAD * CHUNK_LOAD);

        Position position = mPosition.get(player);
        int playerChunkX = RelativeMath.fastfloor(position.x / CHUNK_SIZE);
        int playerChunkY = RelativeMath.fastfloor(position.y / CHUNK_SIZE);

        for (int x = playerChunkX - (CHUNK_LOAD - 1) / 2; x < playerChunkX + (CHUNK_LOAD + 1) / 2; x++) {
            for (int y = playerChunkY - (CHUNK_LOAD - 1) / 2; y < playerChunkY + (CHUNK_LOAD + 1) / 2; y++) {
                Chunk chunk = getChunk(x, y);
                chunkSurrounding.add(chunk);
            }
        }
        return chunkSurrounding;
    }

    public void loadChunk(float x, float y) {
        loadChunk(getChunk(x, y));
    }

    /**
     * Loads the desired chunk from the network or the file system
     *
     * @param chunk chunk which has to be loaded
     */
    public void loadChunk(Chunk chunk) {
        chunk.initialize();
        planetGenerator.generateTerrain(chunk);
        chunkLoader.loadChunkInfo(chunk);
        chunk.loaded = true;
        loadedChunks.add(chunk);
    }

    /**
     * Updates the chunk with the new info which is loaded
     *
     * @param chunk
     */
    public void transferChunk(Chunk chunk) {
        if (chunk != null) {
            Chunk localChunk = getChunk(chunk.x, chunk.y);
            if (chunk.getChangedTiles() != null) {
                localChunk.setChangedTiles(chunk.getChangedTiles());
                updateTiles(localChunk);
            }
        }
    }

    public void unLoadChunk(Chunk chunk) {
        chunk.dispose();
        loadedChunks.removeValue(chunk, false);
    }

    /**
     * Add an entity to the list of entities in a chunk, this entity should be able to move
     *
     * @param e
     */
    public void addEntity(Entity e) {
        Position position = mPosition.get(e);
        if (position != null) {
            addEntity(e, position.x, position.y);
        }
    }

    /**
     * Add an entity to the list of entities in a chunk, this entity should be able to move.
     *
     * @param e
     * @param x
     */
    public void addEntity(Entity e, float x, float y) {
        Chunk chunk = getChunk(x, y);
        UUID entityID = uuidEntityManager.getUuid(e);
        if (!chunk.entities.contains(entityID, false)) {
            chunk.addEntity(entityID);
            mPosition.get(e).chunk = chunk;

            //A check if the entity has permanent authority, if it has permanent authority it has to load the chunks
            //surrounding it
            if (mAuthority.has(e)) {
                if (mAuthority.get(e).type == AuthorityManager.AuthorityType.PERMANENT) {
                    loadChunksAroundEntity(e);
                }
            }
            eventManager.notifyEvent(new JoinChunkEvent(e, chunk));
        }
    }

    /**
     * Remove an entity from a chunk, this doesn't remove the entity itself. It only removes the pointer to the entity.
     *
     * @param e
     */
    public void removeEntity(Entity e) {
        Position position = mPosition.get(e);
        if (position != null) {
            removeEntity(e, position.x, position.y);
        }
    }

    /**
     * Remove an entity from a chunk, this doesn't remove the entity itself. It only removes the pointer to the entity.
     */
    public void removeEntity(Entity e, float x, float y) {
        Chunk chunk = getChunk(x, y);
        UUID entityID = uuidEntityManager.getUuid(e);
        if (chunk.entities.contains(entityID, false)) {
            chunk.removeEntity(entityID);
            eventManager.notifyEvent(new LeaveChunkEvent(e, chunk));
        }
    }

    /**
     * Get the tile from a chunk
     *
     * @param x      coord
     * @param y      coord
     * @return the tile which was requested
     */
    public Entity getTile(float x, float y) {
        Chunk chunk = getChunk(x, y);
        UUID uuid = chunk.getTile(x, y);
        if (uuid != null) {
            return uuidEntityManager.getEntity(uuid);
        } else {
            return null;
        }
    }

    public void addTile(float x, float y, Entity tile) {
        Chunk chunk = getChunk(x, y);
        chunk.addTile(x, y, uuidEntityManager.getUuid(tile));
    }

    public void removeTile(Vector2 tilePos) {
        Chunk chunk = getChunk(tilePos.x, tilePos.y);
        Entity tile = uuidEntityManager.getEntity(chunk.getTile(tilePos.x, tilePos.y));
        //-1 is the value for air.
        chunk.changedTiles.put(tilePos, -1);
        ComponentUtils.removeEntity(tile);
    }

    /**
     * Updates the chunk tiles based on the {@link Chunk#changedTiles}.
     *
     * @param chunk
     */
    public void updateTiles(Chunk chunk) {
        Map<Integer, String> legend = chunk.tileLegend;

        for (Map.Entry<Vector2, Integer> entry : chunk.getChangedTiles().entrySet()) {
            Vector2 position = entry.getKey();
            int newTile = entry.getValue();
            Entity tile = uuidEntityManager.getEntity(chunk.getTile(position.x, position.y));
            ComponentUtils.removeEntity(tile);

            if (newTile != -1) {
                //TODO implement Z?
                tileManager.createTile(chunk.planet, position.x, position.y, 0, legend.get(newTile), false);
            }
        }
    }

    private void checkChunkChange(Entity e, Position position) {
        if (position.chunk != getChunk(position.x, position.y)) {
            removeEntity(e, position.px, position.py);
            addEntity(e, position.x, position.y);
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof NetworkedEntityCreationEvent) {
            addEntity(event.entity);
        } else if (event instanceof NetworkedEntityDeletionEvent) {
            removeEntity(event.entity);
        } else if (event instanceof MovementEvent) {
            MovementEvent movementEvent = (MovementEvent) event;
            checkChunkChange(movementEvent.entity, movementEvent.position);
        }
    }
}
