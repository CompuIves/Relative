package com.ives.relative.factories;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.planet.WorldC;

/**
 * Created by Ives on 13/12/2014.
 */
public class Player {

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
    public static Body createBody(Entity e, float x, float y, float vx, float vy, Entity planet) {
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
        shape.setRadius(0.5f);

        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 2.0f;
        fixtureDef.friction = 0.8f;
        Fixture fixture = body.createFixture(fixtureDef);
/*
        shape = new CircleShape();
        shape.setRadius(0.5f);
        fixtureDef.density = 1.0f;
        body.createFixture(fixtureDef);
*/
        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        return body;
    }
}
