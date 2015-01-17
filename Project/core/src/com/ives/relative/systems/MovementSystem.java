package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.events.MovementEvent;
import com.ives.relative.entities.events.StoppedMovementEvent;
import com.ives.relative.managers.event.EventManager;

/**
 * Created by Ives on 5/12/2014.
 * This system syncs the movements of a entity with its body
 */
@Wire()
public class MovementSystem extends EntityProcessingSystem {
    protected ComponentMapper<Physics> mBodyComponent;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    protected EventManager eventManager;
    protected UuidEntityManager uuidEntityManager;

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

            Vector2 vel = entityBody.getLinearVelocity();
            velocity.vx = vel.x;
            velocity.vy = vel.y;

            if (isMoving(velocity)) {
                sendMovementEvent(e, position, velocity);
                velocity.isMoving = true;
            } else {
                if (velocity.isMoving) {
                    sendNoMovementEvent(e, position);
                    velocity.isMoving = false;
                }
            }
        }
    }

    public boolean isMoving(Velocity v) {
        return Math.abs(v.vx) + Math.abs(v.vy) + Math.abs(v.vr) > 0;
    }

    public void sendMovementEvent(Entity e, Position p, Velocity v) {
        MovementEvent movementEvent = new MovementEvent(e, p, v);
        eventManager.notifyEvent(movementEvent);
    }

    public void sendNoMovementEvent(Entity e, Position p) {
        eventManager.notifyEvent(new StoppedMovementEvent(e, p));
    }
}
