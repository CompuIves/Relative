package com.ives.relative.entities.events.client;

import com.artemis.Entity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 1/2/2015.
 */
public class PlayerConnectedEvent extends EntityEvent {
    public PlayerConnectedEvent(Entity entity) {
        super(entity);
    }
}
