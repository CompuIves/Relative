package com.ives.relative.entities.events.creation;

import com.artemis.Entity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 11/1/2015.
 */
public class NetworkedEntityDeletionEvent extends EntityEvent {
    public NetworkedEntityDeletionEvent(Entity entity) {
        super(entity);
    }
}
