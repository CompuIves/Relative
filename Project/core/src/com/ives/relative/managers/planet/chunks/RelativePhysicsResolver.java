package com.ives.relative.managers.planet.chunks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Ives on 17/1/2015.
 */
public class RelativePhysicsResolver {
    /**
     * Applies a force relative to the body
     *
     * @param x
     * @param y
     * @param rotation in radians
     * @param body
     */
    public static void applyForce(float x, float y, float rotation, Body body) {
        body.applyForceToCenter(rotateCoords(x, y, rotation), true);
    }

    /**
     * Rotates the coordinates based on the
     *
     * @param x
     * @param y
     * @param rotation
     * @return
     */
    public static Vector2 rotateCoords(float x, float y, float rotation) {
        float newX = (float) (x * Math.cos(rotation) - y * Math.sin(rotation));
        float newY = (float) (x * Math.sin(rotation) + y * Math.cos(rotation));
        return new Vector2(newX, newY);
    }
}
