package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.ives.relative.entities.components.body.Position;

/**
 * Created by Ives on 9/1/2015.
 * Gets called once after an entity has stopped moving.
 */
public class StoppedMovementEvent extends EntityEvent {
    public Position position;

    public StoppedMovementEvent(Entity entity, Position p) {
        super(entity);
        this.position = p;
    }
}
