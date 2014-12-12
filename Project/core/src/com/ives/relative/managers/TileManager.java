package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.TileC;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.planet.WorldC;

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
            Entity e = new EntityBuilder(world).with(new TileC(solidTile),
                    new Visual(solidTile.textureRegion, solidTile.width, solidTile.height),
                    new Position(x, y, z, 0, mName.get(planet).internalName)).group("tile").build();
            Body body = createBody(e, solidTile, x, y, gravity, mWorldComponent.get(planet).world);
            e.edit().add(new Physics(body));

            return e;
        } else {
            Gdx.app.error("WorldBuilding", "Couldn't load block with id: " + tileID +
                    " with position " + x + ", " + y + ", " + z + ", ignoring the block for now.");
            return null;
        }
    }

    public Body createBody(Entity e, SolidTile tile, float x, float y, boolean gravity, World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        if (tile.gravity && gravity) {
            e.edit().add(new Velocity(0, 0));
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        } else {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        }
        bodyDef.position.set(x, y);
        Body body = physicsWorld.createBody(bodyDef);
        PolygonShape shape = getCube(tile.width, tile.height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.9f;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        return body;
    }

    public void addTile(String id, SolidTile tile) {
        solidTiles.put(id, tile);
    }
}
