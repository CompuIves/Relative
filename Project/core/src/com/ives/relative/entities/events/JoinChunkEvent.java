package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.ives.relative.managers.planet.Chunk;

/**
 * Created by Ives on 6/1/2015.
 */
public class JoinChunkEvent extends EntityEvent {
    public Chunk chunk;

    public JoinChunkEvent(Entity entity, Chunk chunk) {
        super(entity);
        this.chunk = chunk;
    }
}
