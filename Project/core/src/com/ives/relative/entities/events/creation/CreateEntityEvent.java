package com.ives.relative.entities.events.creation;

import com.artemis.Entity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 11/1/2015.
 */
public class CreateEntityEvent extends EntityEvent {
    public CreateEntityEvent(Entity entity) {
        super(entity);
    }
}
