package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.ives.relative.managers.event.StateManager;

/**
 * Created by Ives on 2/1/2015.
 * <p/>
 * This gets implemented by the classes which wish te be notified by events called on entities, like a jump event.
 */
public interface EntityChangeObserver {
    void onNotify(Entity e, StateManager.EntityState state);
}
