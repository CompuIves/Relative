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
    public int owner;
    public boolean loaded = false;
    private int startX, endX;
    private String planet;
    private transient Map<Vector2, UUID> tiles;
    private Map<Vector2, String> changedTiles;
    private transient Array<UUID> entities;

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
    }

    public int getStartX() {
        return startX;
    }

    public int getEndX() {
        return endX;
    }

    public String getPlanet() {
        return planet;
    }

    public Map<Vector2, UUID> getTiles() {
        return tiles;
    }

    public void setTiles(Map<Vector2, UUID> tiles) {
        this.tiles = tiles;
    }

    public Array<UUID> getEntities() {
        return entities;
    }

    public void setEntities(Array<UUID> entities) {
        this.entities = entities;
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

    public void updateTiles() {

    }

    public void dispose() {
        tiles.clear();
        entities.clear();
        loaded = false;
    }

    public Map<Vector2, String> getChangedTiles() {
        return changedTiles;
    }

    public void setChangedTiles(Map<Vector2, String> changedTiles) {
        this.changedTiles = changedTiles;
        updateTiles();
    }
}