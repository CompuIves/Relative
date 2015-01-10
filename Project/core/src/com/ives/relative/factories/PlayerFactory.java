package com.ives.relative.factories;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.body.FootC;
import com.ives.relative.entities.components.planet.WorldC;

/**
 * Created by Ives on 13/12/2014.
 */
public class PlayerFactory {

    /**
     * Creates a body specific for players
     *
     * @param e
     * @param x
     * @param y
     * @param vx
     * @param vy
     * @param planet
     * @return
     */
    public static Body createBody(Entity e, float x, float y, float vx, float vy, float radius, Entity planet) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearVelocity.set(vx, vy);

        World world = e.getWorld().getMapper(WorldC.class).get(planet).world;

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();
        Shape shape = new CircleShape();
        fixtureDef.shape = shape;
        shape.setRadius(radius);

        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 30.0f;
        fixtureDef.friction = 0.6f;
        fixtureDef.isSensor = true;
        Fixture fixture = body.createFixture(fixtureDef);

        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(0.5f, 0.1f, new Vector2(0, -radius), 0);
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true;
        Fixture sensorFixture = body.createFixture(fixtureDef);

        FootC footC = new FootC();
        footC.yOffset = -radius;
        sensorFixture.setUserData(FootC.class);
        e.edit().add(footC);

        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        sensorShape.dispose();
        return body;
    }
}
