package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;

/**
 * Created by Ives on 5/1/2015.
 * <p/>
 * A simple event which will be executed when an entity moves
 */
public class MovementEvent extends EntityEvent {
    public Position position;
    public Velocity velocity;

    public MovementEvent(Entity entity, Position position, Velocity velocity) {
        super(entity);
        this.position = position;
        this.velocity = velocity;
    }
}
