package com.ives.relative.managers.planet.chunks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
    public int x, y;
    public boolean loaded = false;
    public int z;

    /**
     * Chunks can be rotated (AWESOME!), this is the rotation of the chunk in degrees
     */
    public int rotation;
    public transient Vector2 gravity;
    public String planet;
    /**
     * Every tile in this chunk, the positions are relative to the chunk.
     */
    public transient Map<Vector2, UUID> tiles;

    /**
     * To preserve space when saving, or sending the tiles I don't want to save every changed tile by name. I give every
     * name an id and add it to the changed tiles.
     */
    //TODO move this to the chunkmanager
    public Map<Integer, String> tileLegend;
    /**
     * Every changed tile from generation in a chunk, -1 = air. Relative to the chunk.
     */
    public Map<Vector2, Integer> changedTiles;


    public transient Array<UUID> entities;

    public Chunk() {
    }

    public Chunk(int x, int y, float gravityX, float gravityY, int rotation) {
        this.x = x;
        this.y = y;
        this.gravity = new Vector2(gravityX, gravityY);
        this.rotation = rotation;
        initialize();
    }

    public void initialize() {
        tiles = new HashMap<Vector2, UUID>();
        entities = new Array<UUID>();
        changedTiles = new HashMap<Vector2, Integer>();
        tileLegend = new HashMap<Integer, String>();
    }

    public void addEntity(UUID e) {
        if (!entities.contains(e, false)) {
            entities.add(e);
        }
    }

    public void removeEntity(UUID e) {
        entities.removeValue(e, false);
    }

    public void addTile(float x, float y, UUID tile) {
        this.addTile((int) x, (int) y, tile);
    }

    public void addTile(int x, int y, UUID tile) {
        tiles.put(new Vector2(x, y), tile);
    }

    public void addChangedTile(int x, int y, int tile) {
        changedTiles.put(new Vector2(x, y), tile);
    }

    /**
     * Gets the tile relative to the chunk
     * @param x the x relative to the chunk
     * @param y the y coordinate
     * @return
     */
    public UUID getTile(float x, float y) {
        Vector2 vector2 = new Vector2(RelativeMath.fastfloor(x), RelativeMath.fastfloor(y));
        if (tiles.containsKey(vector2)) {
            //Return the tile relative to the chunk
            return tiles.get(vector2);
        }
        return null;
    }

    public Map<Vector2, Integer> getChangedTiles() {
        return changedTiles;
    }

    public void setChangedTiles(Map<Vector2, Integer> changedTiles) {
        this.changedTiles = changedTiles;
    }

    public void dispose() {
        tiles.clear();
        entities.clear();
        changedTiles.clear();
        loaded = false;
    }
}
