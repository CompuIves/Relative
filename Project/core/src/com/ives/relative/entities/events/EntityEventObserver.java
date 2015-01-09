package com.ives.relative.entities.events;

/**
 * Created by Ives on 5/1/2015.
 */
public interface EntityEventObserver {
    void onNotify(EntityEvent event);
}
