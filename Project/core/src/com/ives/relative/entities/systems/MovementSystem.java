package com.ives.relative.entities.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ives.relative.entities.components.body.PhysicsPosition;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire()
public class MovementSystem extends EntityProcessingSystem {
    protected ComponentMapper<PhysicsPosition> mBodyComponent;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public MovementSystem() {
        super(Aspect.getAspectForAll(PhysicsPosition.class));
    }

    @Override
    protected void process(Entity e) {
        if (!mBodyComponent.get(e).body.getLinearVelocity().isZero()) {

        }
    }
}
