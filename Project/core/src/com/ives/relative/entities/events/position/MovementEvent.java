package com.ives.relative.entities.events.position;

import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 5/1/2015.
 * <p/>
 * A simple event which will be executed when an entity moves
 */
public class MovementEvent extends EntityEvent {
    public MovementEvent() {
    }

    @Override
    public void reset() {
    }
}
