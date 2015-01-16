package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.ives.relative.managers.planet.chunks.Chunk;

/**
 * Created by Ives on 6/1/2015.
 */
public class LeaveChunkEvent extends EntityEvent {
    public Chunk chunk;

    public LeaveChunkEvent(Entity e, Chunk chunk) {
        super(e);
        this.chunk = chunk;
    }
}
