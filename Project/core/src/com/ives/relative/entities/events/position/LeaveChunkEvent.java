package com.ives.relative.entities.events.position;

import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 6/1/2015.
 */
public class LeaveChunkEvent extends EntityEvent {
    public Chunk chunk;

    public LeaveChunkEvent() {
    }

    @Override
    public void reset() {
        chunk = null;
    }
}
