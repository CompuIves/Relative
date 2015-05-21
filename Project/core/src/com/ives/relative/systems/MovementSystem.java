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
import com.ives.relative.entities.events.position.MovementEvent;
import com.ives.relative.entities.events.position.NearUniverseBorderEvent;
import com.ives.relative.entities.events.position.StoppedMovementEvent;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.chunks.ChunkManager;

/**
 * Created by Ives on 5/12/2014.
 * This system syncs the movements of a entity with its body
 */
@Wire
public class MovementSystem extends EntityProcessingSystem {
    private static Vector2 tempVec = new Vector2();

    protected ComponentMapper<Physics> mBodyComponent;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    protected EventManager eventManager;
    protected UuidEntityManager uuidEntityManager;
    protected AuthorityManager authorityManager;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public MovementSystem() {
        //noinspection unchecked
        super(Aspect.getAspectForAll(Physics.class, Position.class));
    }

    @Override
    protected void process(Entity e) {
        Physics physics = mBodyComponent.get(e);
        Position p = mPosition.get(e);

        updatePos(p, physics);

        if (mVelocity.has(e)) {
            Velocity v = mVelocity.get(e);
            updateVelocity(v, physics);
            movementCheck(e, v, p, physics);
            checkBorderEvent(e, p);
        }
    }

    private void updatePos(Position position, Physics physics) {
        Body entityBody = physics.body;
        if (entityBody != null) {
            Vector2 bodyPosition = entityBody.getPosition();
            position.px = position.x;
            position.py = position.y;
            position.x = bodyPosition.x;
            position.y = bodyPosition.y;
            position.protation = position.rotation;
            position.rotation = physics.body.getTransform().getRotation();
        }
    }

    private void updateVelocity(Velocity velocity, Physics physics) {
        Body entityBody = physics.body;
        Vector2 vel = entityBody.getLinearVelocity();
        velocity.vx = vel.x;
        velocity.vy = vel.y;
    }

    private void movementCheck(Entity e, Velocity v, Position p, Physics physics) {
        if (physics.body.isAwake()) {
            sendMovementEvent(e, p, v);
            v.isMoving = true;
        } else {
            if (v.isMoving) {
                sendNoMovementEvent(e, p);
                v.isMoving = false;
            }
        }
    }

    private void checkBorderEvent(Entity e, Position p) {
        if (authorityManager.isEntityTemporaryAuthorized(e)) {
            if (isNearBorder(p.space, tempVec.set(p.x, p.y))) {
                sendBorderEvent(e);
            }
        }
    }

    public void sendMovementEvent(Entity e, Position p, Velocity v) {
        MovementEvent movementEvent = (MovementEvent) eventManager.getEvent(MovementEvent.class, e);
        movementEvent.position = p;
        movementEvent.velocity = v;
        eventManager.notifyEvent(movementEvent);
    }

    public void sendBorderEvent(Entity e) {
        eventManager.notifyEvent(eventManager.getEvent(NearUniverseBorderEvent.class, e));
    }

    private boolean isNearBorder(Space uBod, Vector2 pos) {
        return pos.x > uBod.width / 2 - ChunkManager.CHUNK_RADIUS || pos.x < -uBod.width / 2 + ChunkManager.CHUNK_RADIUS
                || pos.y > uBod.height / 2 - ChunkManager.CHUNK_RADIUS || pos.y < -uBod.width / 2 + ChunkManager.CHUNK_RADIUS;
    }


    public void sendNoMovementEvent(Entity e, Position p) {
        StoppedMovementEvent event = (StoppedMovementEvent) eventManager.getEvent(StoppedMovementEvent.class, e);
        event.position = p;
        eventManager.notifyEvent(event);
    }
}
