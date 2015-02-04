package com.ives.relative.entities.events.position;

import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 6/1/2015.
 * Gets called when an entity joins a chunk.
 */
public class JoinChunkEvent extends EntityEvent {
    public Chunk chunk;

    public JoinChunkEvent() {
    }

    @Override
    public void reset() {
        chunk = null;
    }
}
