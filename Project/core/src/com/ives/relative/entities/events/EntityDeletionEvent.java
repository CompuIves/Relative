package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 10/1/2015.
 */
public class EntityDeletionEvent extends EntityEvent {

    public EntityDeletionEvent(Entity entity) {
        super(entity);
    }
}
