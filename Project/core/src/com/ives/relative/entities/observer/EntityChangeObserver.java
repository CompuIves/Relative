package com.ives.relative.entities.observer;

import com.badlogic.ashley.core.Entity;

/**
 * Created by Ives on 11/12/2014.
 */
public interface EntityChangeObserver {
    public void onEntityChange(Entity entity, Event event);

    enum Event {
        MOVED
    }
}