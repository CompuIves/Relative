package com.ives.relative.managers.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
    public boolean loaded = false;
    public int startX, endX;
    public int z;
    public String planet;
    public transient Map<Vector2, UUID> tiles;

    /**
     * To preserve space when saving, or sending the tiles I don't want to save every changed tile by name. I give every
     * name an id and add it to the changed tiles.
     */
    public Map<Integer, String> tileLegend;
    /**
     * Every changed tile from generation in a chunk, -1 = air.
     */
    public Map<Vector2, Integer> changedTiles;


    public transient Array<UUID> entities;

    public Chunk() {
    }

    public Chunk(int startX, int endX, String planet) {
        this.startX = startX;
        this.endX = endX;
        this.planet = planet;
        initialize();
    }

    public void initialize() {
        tiles = new HashMap<Vector2, UUID>();
        entities = new Array<UUID>();
        changedTiles = new HashMap<Vector2, Integer>();
        tileLegend = new HashMap<Integer, String>();
    }

    public void addEntity(UUID e) {
        System.out.println("Added an entity to chunk " + startX + " to " + endX + "!");
        if (!entities.contains(e, false)) {
            entities.add(e);
        }
    }

    public void removeEntity(UUID e) {
        entities.removeValue(e, false);
    }

    public void addTile(float x, float y, UUID tile) {
        if (isThisChunk(x)) {
            tiles.put(new Vector2(x, y), tile);
        }
    }

    public UUID getTile(float x, float y) {
        if (isThisChunk(x)) {
            Vector2 vector2 = new Vector2((int) x, (int) y);
            if (tiles.containsKey(vector2)) {
                return tiles.get(vector2);
            }
        }
        return null;
    }

    public boolean isThisChunk(float x) {
        return x >= startX && x <= endX;
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
