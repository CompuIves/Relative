package com.ives.relative.universe.chunks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 4/1/2015.
 * <p/>
 * A simple chunk, the world is divided in chunks and these chunks contain some information about what's in them,
 * the advantage of this is that if I have to check for entities near a player I don't have to search the whole world,
 * I can just search in the nearby chunks for entities.
 */
public class Chunk {
    public final int x, y;
    public final UniverseBody universeBody;
    public final int rotation;
    public final Array<UUID> entities;
    public final Map<Vector2, UUID> tiles;
    public final Map<Vector2, Integer> changedTiles;
    public boolean loaded = false;
    private int playerAmount = 0;
    private Vector2 gravity;
    /**
     * Is this chunk part of a planet (ex: should there be dirt?)
     */
    private boolean isLand = false;

    /**
     * @param x
     * @param y
     * @param universeBody
     * @param rotation     rotation in degrees
     * @param isLand
     * @param gravityX
     * @param gravityY
     */
    public Chunk(UniverseBody universeBody, int x, int y, int rotation, boolean isLand, float gravityX, float gravityY) {
        this.x = x;
        this.y = y;
        this.universeBody = universeBody;
        this.rotation = rotation;
        this.gravity = new Vector2(gravityX, gravityY);
        this.isLand = isLand;

        entities = new Array<UUID>();
        changedTiles = new HashMap<Vector2, Integer>();
        tiles = new HashMap<Vector2, UUID>();
    }

    public void addEntity(UUID e) {
        if (!entities.contains(e, false)) {
            entities.add(e);
        }
    }

    public void removeEntity(UUID e) {
        entities.removeValue(e, false);
    }

    public UUID getTile(int x, int y) {
        return tiles.get(new Vector2(x, y));
    }

    public void addTile(int x, int y, UUID tile) {
        if (isInChunk(x, y)) {
            tiles.put(new Vector2(x, y), tile);
        }
    }

    /**
     * This adds the specified tile to changedTiles.
     * <p>REMEMBER, THIS ONLY ADDS A TILE TO MAP, YOU HAVE TO REMOVE THE OLD TILE MANUALLY</p>
     *
     * @param x
     * @param y
     * @param newTile
     */
    public void replaceTile(int x, int y, int newTile) {
        if (isInChunk(x, y) && tiles.containsKey(new Vector2(x, y))) {
            changedTiles.put(new Vector2(x, y), newTile);
        }
    }

    public boolean isLand() {
        return isLand;
    }

    public void addOnePlayer() {
        playerAmount++;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    private boolean isInChunk(int x, int y) {
        return RelativeMath.isInBounds(x, this.x, this.x + ChunkManager.CHUNK_SIZE)
                && RelativeMath.isInBounds(y, this.y, this.y + ChunkManager.CHUNK_SIZE);
    }

    public void dispose() {
        playerAmount = 0;
        tiles.clear();
        loaded = false;
    }
}
