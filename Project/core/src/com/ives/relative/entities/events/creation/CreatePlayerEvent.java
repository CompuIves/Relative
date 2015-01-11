package com.ives.relative.entities.events.creation;

import com.artemis.Entity;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 7/1/2015.
 */
public class CreatePlayerEvent extends EntityEvent {
    public CreatePlayerEvent(Entity entity) {
        super(entity);
    }
}
