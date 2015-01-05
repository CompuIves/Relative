package com.ives.relative.entities.events;

import com.artemis.Entity;

/**
 * Created by Ives on 5/1/2015.
 */
public interface EntityEventObserver {
    void onNotify(Entity e, EntityEvent event);
}
