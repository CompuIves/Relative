package com.ives.relative.entities.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.TileComponent;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.body.PhysicsPosition;
import com.ives.relative.entities.components.planet.WorldComponent;
import com.ives.relative.entities.factories.Tile;

import java.util.HashMap;

/**
 * Created by Ives on 11/12/2014.
 */
@Wire
public class TileManager extends Manager {
    public HashMap<String, SolidTile> solidTiles;
    protected ComponentMapper<WorldComponent> mWorldComponent;
    private Tile tile;

    public TileManager() {
        solidTiles = new HashMap<String, SolidTile>();
    }

    public static PolygonShape getCube(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    public Entity createTile(Entity planet, float x, float y, int z, String tileID, boolean gravity) {

        if (solidTiles.get(tileID) != null) {
            SolidTile solidTile = solidTiles.get(tileID);
            //TODO Look at factories
            Entity e = world.createEntity();
            e.edit().add(new TileComponent(solidTile)).add(new VisualComponent(solidTile.textureRegion, solidTile.width, solidTile.height));
            Body body = createBody(e, solidTile, x, y, z, gravity, mWorldComponent.get(planet).world);
            e.edit().add(new PhysicsPosition(body, z, world.getManager(PlanetManager.class).getPlanetID(planet)));

            return e;
        } else {
            Gdx.app.error("WorldBuilding", "Couldn't load block with id: " + tileID +
                    " with position " + x + ", " + y + ", " + z + ", ignoring the block for now.");
            return null;
        }
    }

    public Body createBody(Entity e, SolidTile tile, float x, float y, int z, boolean gravity, World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        if (tile.gravity && gravity)
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        else
            bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = physicsWorld.createBody(bodyDef);
        PolygonShape shape = getCube(tile.width, tile.height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.7f;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        return body;
    }

    public void addTile(String id, SolidTile tile) {
        solidTiles.put(id, tile);
    }
}
