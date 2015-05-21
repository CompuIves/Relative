package com.ives.relative.entities.events.position;

import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.universe.Space;

/**
 * Created by Ives on 5/2/2015.
 *
 * When an Entity hits the border of a Space
 */
public class UniverseBodyCollisionEvent extends EntityEvent {
    public Space space;

    @Override
    public void reset() {
        space = null;
    }
}
