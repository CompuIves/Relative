package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 5/1/2015.
 */
public abstract class EntityEvent {
    public Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }
}
