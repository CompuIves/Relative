package com.ives.relative.universe.planets;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.TileFactory;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 11/12/2014.
 * The manager of all tiles
 */
@Wire
public class TileManager extends Manager {
    public HashMap<String, SolidTile> solidTiles;

    protected ComponentMapper<TileC> mTileC;

    protected NetworkManager networkManager;
    protected ChunkManager chunkManager;
    protected UuidEntityManager uuidEntityManager;

    public TileManager() {
        solidTiles = new HashMap<String, SolidTile>();
    }

    /**
     * Creates a tile at the given coordinates
     *
     * @param x       x coord
     * @param y       y coord
     * @param z       z coord
     * @param tileID  name of tile
     * @return the entity of the tile created
     */
    public Entity createTile(UniverseBody ub, float x, float y, int z, String tileID, boolean gravity) {
        if (solidTiles.get(tileID) != null) {
            SolidTile solidTile = solidTiles.get(tileID);
            //TODO Look at factories
            Entity e = new EntityBuilder(world).with(new TileC(solidTile),
                    new Visual(solidTile.textureRegion, solidTile.width, solidTile.height),
                    new Position(x, y, z, 0, ub)).group("tile").build();

            if (gravity) {
                Body body = TileFactory.createBody(ub, e, solidTile, 15, x, y, true);
                e.edit().add(new Velocity()).add(new Physics(body, BodyDef.BodyType.DynamicBody));
                int networkID = networkManager.addEntity(e);
                e.edit().add(new NetworkC(networkID, 1, NetworkManager.Type.TILE));
                chunkManager.addEntityToChunk(e);
            }

            return e;
        } else {
            Gdx.app.error("WorldBuilding", "Couldn't load block with id: " + tileID +
                    " with position " + x + ", " + y + ", " + z + ", ignoring the block for now.");
            return null;
        }
    }

    public void addTile(String id, SolidTile tile) {
        solidTiles.put(id, tile);
    }

    public void removeTile(Chunk chunk, Vector2 tilePos) {
        UUID tile = chunk.getTile((int) tilePos.x, (int) tilePos.y);
        ComponentUtils.removeEntity(uuidEntityManager.getEntity(tile));
        chunk.removeTile(tilePos);
    }

    public Entity getTile(Chunk chunk, Vector2 tilePos) {
        UUID tile = chunk.getTile((int) tilePos.x, (int) tilePos.y);
        if (tile != null) {
            return uuidEntityManager.getEntity(tile);
        }

        return null;
    }

    public void generateTileBodies(Chunk chunk) {
        for (Map.Entry t : chunk.tiles.entrySet()) {
            Vector2 pos = (Vector2) t.getKey();
            Entity tile = uuidEntityManager.getEntity((UUID) t.getValue());
            int contour = 0;
            if (chunk.isTile((int) pos.x, (int) pos.y + 1)) {
                contour += 1;
            }
            if (chunk.isTile((int) pos.x + 1, (int) pos.y)) {
                contour += 2;
            }
            if (chunk.isTile((int) pos.x, (int) pos.y - 1)) {
                contour += 4;
            }
            if (chunk.isTile((int) pos.x - 1, (int) pos.y)) {
                contour += 8;
            }

            if (contour == 0)
                contour = 15;

            TileC tileC = mTileC.get(tile);
            Body body = TileFactory.createBody(chunk.universeBody, tile, tileC.tile, contour, pos.x, pos.y, false);

            tile.edit().add(new Physics(body, BodyDef.BodyType.StaticBody));
        }
    }
}
