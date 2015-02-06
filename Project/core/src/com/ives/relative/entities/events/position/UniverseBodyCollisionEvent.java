package com.ives.relative.entities.events.position;

import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.universe.UniverseBody;

/**
 * Created by Ives on 5/2/2015.
 */
public class UniverseBodyCollisionEvent extends EntityEvent {
    public UniverseBody universeBody;
    public boolean start;

    @Override
    public void reset() {
        universeBody = null;
    }
}
