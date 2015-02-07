package com.ives.relative.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.universe.UniverseBody;

/**
 * Created by Ives on 7/2/2015.
 * <p/>
 * Utilities for bodies, like copying bodies or transforming bodies to eachother.
 */
public class BodyUtils {

    /**
     * Transforms the positions from the first body over to the second body by transforming the coordinates of the UniverseBody
     * of the first body to the UniverseBody of the second UniverseBody
     *
     * @param fromBody         Body to copy the positions from
     * @param toBody           Body to copy to positions to
     * @param fromUniverseBody UniverseBody of the fromBody
     * @param toUniverseBody   UniverseBody of the ToBody
     */
    public static void transformBody(Body fromBody, Body toBody, UniverseBody fromUniverseBody, UniverseBody toUniverseBody) {
        Vector3 pos = new Vector3(fromBody.getPosition(), fromBody.getAngle() * MathUtils.radiansToDegrees);
        fromUniverseBody.transformVectorToUniverseBody(toUniverseBody, pos);

        Vector2 vel = fromBody.getLinearVelocity();
        fromUniverseBody.transformVectorToUniverseBody(toUniverseBody, vel);
        toBody.setLinearVelocity(vel);
        toBody.setTransform(pos.x, pos.y, pos.z * MathUtils.degreesToRadians);
    }

    /**
     * Converts a Fixture to a FixtureDef
     *
     * @param fixture Fixture to convert
     * @return FixtureDef with configuration of fixture + shape
     */
    private static FixtureDef copyFixture(Fixture fixture) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = fixture.getShape();
        fixtureDef.isSensor = fixture.isSensor();
        fixtureDef.friction = fixture.getFriction();
        fixtureDef.density = fixture.getDensity();
        fixtureDef.restitution = fixture.getRestitution();
        return fixtureDef;
    }

    /**
     * Copies every property from the body and creates a second body from it.
     *
     * @param newWorld The world which has to create the new body
     * @param body     The body to copy from
     * @return The new body
     */
    public static Body copyBody(World newWorld, Body body) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.linearDamping = body.getLinearDamping();
        bodyDef.angularDamping = body.getAngularDamping();
        bodyDef.type = body.getType();
        bodyDef.linearVelocity.set(body.getLinearVelocity());
        bodyDef.fixedRotation = body.isFixedRotation();
        bodyDef.active = body.isActive();
        bodyDef.allowSleep = body.isSleepingAllowed();
        bodyDef.bullet = body.isBullet();
        bodyDef.gravityScale = body.getGravityScale();

        Body copyBody = newWorld.createBody(bodyDef);
        copyBody.setUserData(body.getUserData());

        for (Fixture fixture : body.getFixtureList()) {
            Fixture copyFixture = copyBody.createFixture(copyFixture(fixture));
            copyFixture.setUserData(fixture.getUserData());
        }

        return copyBody;
    }
}
