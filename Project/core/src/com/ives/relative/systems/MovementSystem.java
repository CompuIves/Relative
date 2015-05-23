package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.events.position.MovementEvent;
import com.ives.relative.entities.events.position.StoppedMovementEvent;
import com.ives.relative.managers.event.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ives on 5/12/2014.
 * This system syncs the movements of a entity with its body
 */
@Wire
public class MovementSystem extends EntityProcessingSystem {
    protected ComponentMapper<Physics> mBodyComponent;
    protected ComponentMapper<Location> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    protected EventManager eventManager;
    protected UuidEntityManager uuidEntityManager;

    private final List<UUID> movingEntities;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public MovementSystem() {
        //noinspection unchecked
        super(Aspect.getAspectForAll(Physics.class, Location.class));
        movingEntities = new ArrayList<UUID>();
    }

    @Override
    protected void process(Entity e) {
        Physics physics = mBodyComponent.get(e);

        if (mVelocity.has(e)) {
            movementCheck(e, physics);
        }
    }

    private void movementCheck(Entity e, Physics physics) {
        if (physics.body.isAwake()) {
            sendMovementEvent(e);
            movingEntities.add(e.getUuid());
        } else {
            if (movingEntities.contains(e.getUuid())) {
                sendNoMovementEvent(e);
                movingEntities.remove(e.getUuid());
            }
        }
    }

    public void sendMovementEvent(Entity e) {
        MovementEvent movementEvent = (MovementEvent) eventManager.getEvent(MovementEvent.class, e);
        eventManager.notifyEvent(movementEvent);
    }

    public void sendNoMovementEvent(Entity e) {
        StoppedMovementEvent event = (StoppedMovementEvent) eventManager.getEvent(StoppedMovementEvent.class, e);
        eventManager.notifyEvent(event);
    }
}
