package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 7/1/2015.
 */
public class CreatePlayerEvent extends EntityEvent {
    public CreatePlayerEvent(Entity entity) {
        super(entity);
    }
}
