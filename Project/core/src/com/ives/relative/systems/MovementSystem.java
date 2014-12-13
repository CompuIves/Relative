package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;

/**
 * Created by Ives on 5/12/2014.
 * This system syncs the movements of a entity with its body
 */
@Wire()
public class MovementSystem extends EntityProcessingSystem {
    protected ComponentMapper<Physics> mBodyComponent;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public MovementSystem() {
        //noinspection unchecked
        super(Aspect.getAspectForAll(Physics.class, Position.class, Velocity.class));
    }

    @Override
    protected void process(Entity e) {
        Physics physics = mBodyComponent.get(e);
        Position position = mPosition.get(e);
        Velocity velocity = mVelocity.get(e);

        Body entityBody = physics.body;
        if (entityBody != null) {
            Vector2 bodyPosition = entityBody.getPosition();
            position.px = position.x;
            position.py = position.y;
            position.x = bodyPosition.x;
            position.y = bodyPosition.y;
            position.protation = position.rotation;
            position.rotation = physics.body.getTransform().getRotation();
            velocity.vx = entityBody.getLinearVelocity().x;
            velocity.vy = entityBody.getLinearVelocity().y;
        }
    }
}
