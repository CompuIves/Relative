package com.ives.relative.entities.events.creation;

import com.artemis.Entity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 10/1/2015.
 */
public class EntityDeletionEvent extends EntityEvent {

    public EntityDeletionEvent(Entity entity) {
        super(entity);
    }
}
