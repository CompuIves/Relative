package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 11/1/2015.
 */
public class CreateEntityEvent extends EntityEvent {
    public CreateEntityEvent(Entity entity) {
        super(entity);
    }
}
