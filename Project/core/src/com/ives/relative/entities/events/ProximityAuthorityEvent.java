package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 7/1/2015.
 */
public class ProximityAuthorityEvent extends EntityEvent {
    public boolean start;
    public Entity object;

    public ProximityAuthorityEvent(Entity entity, Entity object, boolean start) {
        super(entity);
        this.object = object;
        this.start = start;
    }
}
