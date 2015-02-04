package com.ives.relative.universe.chunks;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
public class Chunk implements Comparable<Chunk> {
    public final int x, y;
    public final int width, height;
    public final UniverseBody universeBody;
    public final int rotation;
    public final Array<UUID> entities;
    public final Map<Vector2, UUID> tiles;
    public final Map<Vector2, Integer> changedTiles;
    public final Vector2 gravity;
    public final boolean edge;
    public boolean loaded = false;
    public Pixmap bgColor;
    public Texture texture;

    private Array<UUID> loadedByPlayers;

    /**
     * @param x
     * @param y
     * @param universeBody
     * @param rotation     rotation in degrees
     * @param gravityX
     * @param gravityY
     */
    public Chunk(UniverseBody universeBody, int x, int y, int width, int height, int rotation, boolean edge, float gravityX, float gravityY) {
        this.x = x;
        this.y = y;
        this.universeBody = universeBody;
        this.rotation = rotation;
        this.gravity = new Vector2(gravityX, gravityY);
        this.edge = edge;

        entities = new Array<UUID>();
        changedTiles = new HashMap<Vector2, Integer>();
        tiles = new HashMap<Vector2, UUID>();

        loadedByPlayers = new Array<UUID>();

        this.width = width;
        this.height = height;
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

    public void removeTile(Vector2 tile) {
        tiles.remove(tile);
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

    /**
     * Adds a player to the loaded list. This list keeps track of who loaded this chunk. Is used for unloading.
     *
     * @param e
     */
    public void addLoadedByPlayer(UUID e) {
        if (!loadedByPlayers.contains(e, false))
            loadedByPlayers.add(e);
    }

    /**
     * Removes a player of the loaded list. See {@link #addLoadedByPlayer(UUID)} for more info about the list.
     *
     * @param e
     */
    public void removeLoadedByPlayer(UUID e) {
        loadedByPlayers.removeValue(e, false);
    }

    /**
     * @return amount of players currently having this chunk loaded
     */
    public int getLoadedByPlayersSize() {
        return loadedByPlayers.size;
    }

    private boolean isInChunk(int x, int y) {
        return RelativeMath.isInBounds(x, this.x, this.x + width)
                && RelativeMath.isInBounds(y, this.y, this.y + height);
    }

    public void dispose() {
        loadedByPlayers.clear();
        entities.clear();
        tiles.clear();
        loaded = false;
    }

    @Override
    public String toString() {
        return "Chunk from UniverseBody " + universeBody.id + " with x " + x + " and y " + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chunk chunk = (Chunk) o;

        if (x != chunk.x) return false;
        if (y != chunk.y) return false;
        if (!universeBody.equals(chunk.universeBody)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + universeBody.hashCode();
        return result;
    }

    @Override
    public int compareTo(Chunk o) {
        return universeBody.depth - o.universeBody.depth;
    }
}
