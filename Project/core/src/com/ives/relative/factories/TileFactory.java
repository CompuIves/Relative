package com.ives.relative.factories;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.utils.ShapeUtils;

/**
 * Created by Ives on 13/12/2014.
 */
public class TileFactory {

    /**
     * Creates a body specific for tiles
     *
     * @param e
     * @param tile
     * @param x
     * @param y
     * @param gravity      if this tile should be affected by gravity (when the tile itself can also be affected by gravity)
     * @return
     */
    public static Body createBody(UniverseBody universeBody, Entity e, SolidTile tile, int contour, float x, float y, boolean gravity) {
        World physicsWorld = universeBody.world;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = gravity ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = physicsWorld.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.set(ShapeUtils.getContourVertices(contour, tile.width, tile.height));
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 6.0f;
        fixtureDef.friction = tile.friction;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        return body;
    }
}
