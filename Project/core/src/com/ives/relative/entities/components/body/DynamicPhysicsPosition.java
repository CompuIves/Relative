package com.ives.relative.entities.components.body;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Ives on 10/12/2014.
 */
public class DynamicPhysicsPosition extends PhysicsPosition {
    public DynamicPhysicsPosition(Body body, int z, String worldID) {
        super(body, z, worldID);
    }
}
