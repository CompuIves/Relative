package com.ives.relative.factories;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.managers.SolidTile;
import com.ives.relative.managers.TileManager;

/**
 * Created by Ives on 13/12/2014.
 */
public class Tile {
    public static Body createBody(Entity e, SolidTile tile, float x, float y, boolean gravity, World physicsWorld) {
        BodyDef bodyDef = new BodyDef();
        if (tile.gravity && gravity) {
            e.edit().add(new Velocity(0, 0));
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        } else {
            bodyDef.type = BodyDef.BodyType.StaticBody;
        }
        bodyDef.position.set(x, y);
        Body body = physicsWorld.createBody(bodyDef);
        PolygonShape shape = TileManager.getCube(tile.width, tile.height);
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
}
