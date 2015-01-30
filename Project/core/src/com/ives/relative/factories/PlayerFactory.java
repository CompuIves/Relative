package com.ives.relative.factories;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.body.FootC;
import com.ives.relative.universe.UniverseBody;

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
     * @return
     */
    public static Body createBody(UniverseBody universeBody, Entity e, float x, float y, float vx, float vy) {
        BodyDef bodyDef = new BodyDef();
        World world = universeBody.world;
        float height = 1.8f;
        float width = 0.9f;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearVelocity.set(vx, vy);

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();

        CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(0, -height / 2 + 0.45f));
        fixtureDef.shape = shape;
        shape.setRadius(0.45f);
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.6f;
        Fixture fixture = body.createFixture(fixtureDef);

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(width / 2, height / 2 - 0.25f);
        fixtureDef.shape = playerShape;
        fixtureDef.density = 30.0f;
        fixtureDef.friction = 0.0f;
        Fixture playerBodyFixture = body.createFixture(fixtureDef);
        playerBodyFixture.setUserData(e);

        PolygonShape sensorShape = new PolygonShape();
        sensorShape.setAsBox(width / 2 - 0.05f, 0.1f, new Vector2(0, -height / 2 - 0.1f), 0);
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true;
        Fixture sensorFixture = body.createFixture(fixtureDef);

        FootC footC = new FootC();
        footC.yOffset = -height / 2 - 0.1f;
        sensorFixture.setUserData(FootC.class);
        e.edit().add(footC);

        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        sensorShape.dispose();
        playerShape.dispose();
        return body;
    }
}
