package com.ives.relative.managers.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.TileFactory;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.managers.planet.chunks.ChunkManager;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;

/**
 * Created by Ives on 11/12/2014.
 * The manager of all tiles
 */
@Wire
public class TileManager extends Manager {
    public HashMap<String, SolidTile> solidTiles;
    protected ComponentMapper<WorldC> mWorldComponent;
    protected ComponentMapper<Name> mName;
    protected ComponentMapper<Position> mPosition;

    protected NetworkManager networkManager;
    protected ChunkManager chunkManager;
    protected PlanetManager planetManager;

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
     * @param planet  which planet it needs to place the tile
     * @param x       x coord
     * @param y       y coord
     * @param z       z coord
     * @param tileID  name of tile
     * @return the entity of the tile created
     */
    public Entity createTile(String planet, float x, float y, int z, String tileID, boolean gravity) {
        if (solidTiles.get(tileID) != null) {

            SolidTile solidTile = solidTiles.get(tileID);
            //TODO Look at factories
            Entity e = new EntityBuilder(world).with(new TileC(solidTile),
                    new Visual(solidTile.textureRegion, solidTile.width, solidTile.height),
                    new Position(x, y, z, 0, planet)).group("tile").build();

            Body body = TileFactory.createBody(e, solidTile, x, y, gravity, mWorldComponent.get(planetManager.getPlanet(planet)).world);
            e.edit().add(new Physics(body, gravity ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody));

            if (gravity) {
                e.edit().add(new Velocity());
                int networkID = networkManager.addEntity(e);
                e.edit().add(new NetworkC(networkID, 1, NetworkManager.Type.TILE));

                chunkManager.addEntity(e);
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

    public void removeTile(Vector2 tilePos) {
        chunkManager.removeTile(tilePos);
    }

    public void removeTile(Entity tile) {
        ComponentUtils.removeEntity(tile);
    }

    public Entity getTile(Vector2 tilePos) {
        return chunkManager.getTile(tilePos.x, tilePos.y);
    }
}
