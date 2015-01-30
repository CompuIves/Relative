package com.ives.relative.universe.planets;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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
import java.util.UUID;

/**
 * Created by Ives on 11/12/2014.
 * The manager of all tiles
 */
@Wire
public class TileManager extends Manager {
    public HashMap<String, SolidTile> solidTiles;

    protected NetworkManager networkManager;
    protected ChunkManager chunkManager;
    protected UuidEntityManager uuidEntityManager;

    public TileManager() {
        solidTiles = new HashMap<String, SolidTile>();
    }

    public static PolygonShape createCubeShape(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    public static PolygonShape createTriangleShape(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-width / 2, -height / 2);
        vertices[1] = new Vector2(width / 2, -height / 2);
        vertices[2] = new Vector2(-width / 2, height / 2);
        polygonShape.set(vertices);
        return polygonShape;
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

            Body body = TileFactory.createBody(ub, e, solidTile, x, y, gravity);
            e.edit().add(new Physics(body, gravity ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody));

            if (gravity) {
                e.edit().add(new Velocity());
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
}
