package com.ives.relative.managers.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.planet.ChunkC;
import com.ives.relative.entities.events.*;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.planet.chunkloaders.ChunkLoader;
import com.ives.relative.utils.ComponentUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 4/1/2015.
 * <p></p>
 * This manager sorts every chunk and creates chunks when appropriate.
 */
@Wire
public class ChunkManager extends Manager implements EntityEventObserver {
    public static final int CHUNK_SIZE = 128;
    public static final int CHUNK_LOAD = 3;

    protected PlanetManager planetManager;
    protected PlanetGenerator planetGenerator;
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;

    protected ComponentMapper<ChunkC> mChunkC;
    protected ComponentMapper<Position> mPosition;

    protected ChunkLoader chunkLoader;

    public ChunkManager(ChunkLoader chunkLoader) {
        this.chunkLoader = chunkLoader;
    }

    @Override
    protected void initialize() {
        super.initialize();
        eventManager.addObserver(this);
    }

    /**
     * Simple equation to check which chunk to get
     *
     * @param x
     * @return
     */
    public int getChunkIndex(float x) {
        return (int) x / CHUNK_SIZE;
    }


    public void updateChunk(Chunk chunk) {
        if (chunk != null) {
            Chunk oldChunk = getChunk(chunk.getStartX(), chunk.getPlanet());
            if (chunk.getChangedTiles() != null)
                oldChunk.setChangedTiles(chunk.getChangedTiles());
        }
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
            Chunk chunk = new Chunk(index * CHUNK_SIZE, index * CHUNK_SIZE + CHUNK_SIZE, planet);
            chunks.put(index, chunk);
            return chunk;
        }
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

    public Array<Chunk> getChunks(float startX, float endX, String planet) {
        Array<Chunk> chunks = new Array<Chunk>();
        for (int i = 0; i < (endX - startX) / CHUNK_SIZE; i++) {
            chunks.add(getChunk(startX + i * CHUNK_SIZE, planet));
        }
        return chunks;
    }


    /**
     * Loads the chunks around the player based on a radius of {@link com.ives.relative.managers.planet.ChunkManager#CHUNK_LOAD}.
     * There is a check built in if the chunk is already loaded, there is no need to check for it again.
     * @param player player to be based around
     */
    public void loadChunksAroundPlayer(Entity player) {
        for (Chunk chunk : getChunksSurroundingPlayer(player)) {
            loadChunk(chunk);
        }
    }

    public Array<Chunk> getChunksSurroundingPlayer(Entity player) {
        Array<Chunk> chunkSurrounding = new Array<Chunk>();

        Position position = mPosition.get(player);
        int baseIndex = getChunkIndex(position.x);
        //For example i = -(3 - 1) / 2 = -1 ; i < 2; i++
        for (int i = -(CHUNK_LOAD - 1) / 2; i < (CHUNK_LOAD + 1) / 2; i++) {
            int index = baseIndex + i;

            Chunk chunk = getChunk(index * CHUNK_SIZE, position.planet);
            chunkSurrounding.add(chunk);
        }

        return chunkSurrounding;
    }

    /**
     * Loads the desired chunk
     *
     * @param chunk chunk which has to be loaded
     */
    public void loadChunk(Chunk chunk) {
        if (chunk != null) {
            if (!chunk.loaded) {
                chunk.initialize();
                planetGenerator.generateTerrain(chunk);
                chunkLoader.loadChunkInfo(chunk);
                chunk.loaded = true;
            }
        }
    }

    public void unLoadChunk(float x, String planet) {
        if (isChunkLoaded(x, planet)) {
            Chunk chunk = getChunk(x, planet);
            chunk.dispose();
            mChunkC.get(planetManager.getPlanet(planet)).chunks.remove(getChunkIndex(x));
        }
    }

    public boolean isChunkLoaded(float x, String planet) {
        return getChunks(planet).containsKey(getChunkIndex(x));
    }


    /* I now just pass on the player which wants to load the chunk, this player has to control the chunk too.
    public void findNewOwner(Chunk chunk) {
        Array<Entity> players = serverPlayerManager.getPlayers();
        float smallestDistance = Float.MAX_VALUE;
        Entity smallestEntity = null;
        float middleX = chunk.getStartX() + chunk.getEndX() / 2;
        for(Entity player : players) {
            Position position = mPosition.get(player);
            float distance = Math.abs(middleX - position.x);
            if(distance < smallestDistance) {
                smallestEntity = player;
                smallestDistance = distance;
            }
        }

        int owner = serverPlayerManager.getConnectionByPlayer(smallestEntity);
        if(owner != -1) {
            chunk.setOwner(owner);
        }
    }
    */

    /**
     * Add an entity to the list of entities in a chunk, this entity should be able to move
     *
     * @param e
     */
    public void addEntity(Entity e) {
        Position position = mPosition.get(e);
        if (position != null) {
            addEntity(e, position.x, position.planet);
        }
    }

    /**
     * Add an entity to the list of entities in a chunk, this entity should be able to move.
     * @param e
     * @param x
     * @param world
     */
    public void addEntity(Entity e, float x, String world) {
        Chunk chunk = getChunk(x, world);
        UUID entityID = uuidEntityManager.getUuid(e);
        if (!chunk.getEntities().contains(entityID, false)) {
            chunk.addEntity(uuidEntityManager.getUuid(e));
            eventManager.notifyEvent(e, new JoinChunkEvent(e, chunk));
        }
    }

    /**
     * Remove an entity from a chunk, this doesn't remove the entity itself. It only removes the pointer to the entity.
     * @param e
     */
    public void removeEntity(Entity e) {
        Position position = mPosition.get(e);
        if (position != null) {
            removeEntity(e, position.x, position.planet);
        }
    }

    /**
     * Remove an entity from a chunk, this doesn't remove the entity itself. It only removes the pointer to the entity.
     */
    public void removeEntity(Entity e, float x, String world) {
        Chunk chunk = getChunk(x, world);
        UUID entityID = uuidEntityManager.getUuid(e);
        if (chunk.getEntities().contains(entityID, false)) {
            chunk.removeEntity(uuidEntityManager.getUuid(e));
            eventManager.notifyEvent(e, new LeaveChunkEvent(e, chunk));
        }
    }

    /**
     * Get the tile from a chunk
     *
     * @param x coord
     * @param y coord
     * @param planet name of the planet
     * @return the tile which was requested
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

    public void removeTile(Vector2 tilePos, String planet) {
        Chunk chunk = getChunk(tilePos.x, planet);
        Entity tile = uuidEntityManager.getEntity(chunk.getTile(tilePos.x, tilePos.y));
        ComponentUtils.removeEntity(tile);
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {
        if (event instanceof CreatePlayerEvent) {
            loadChunksAroundPlayer(e);
            addEntity(e);
        } else if (event instanceof JoinChunkEvent) {
            loadChunksAroundPlayer(e);
        }
    }
}
