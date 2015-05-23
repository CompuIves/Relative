package com.ives.relative.entities.events.position;

import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 9/1/2015.
 * Gets called once after an entity has stopped moving.
 */
public class StoppedMovementEvent extends EntityEvent {
    public Location position;

    public StoppedMovementEvent() {
    }

    @Override
    public void reset() {
        position = null;
    }

}
