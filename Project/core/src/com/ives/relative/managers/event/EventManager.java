package com.ives.relative.managers.event;

import com.artemis.Entity;
import com.artemis.Manager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;

/**
 * Created by Ives on 5/1/2015.
 */
public class EventManager extends Manager {
    protected final Array<EntityEventObserver> eventObservers;

    public EventManager() {
        eventObservers = new Array<EntityEventObserver>();
    }

    /**
     * This notifies every interested observer of an event which happened to an entity
     *
     * @param e
     * @param event
     */
    public void notifyEvent(Entity e, EntityEvent event) {
        if (e != null) {
            for (int i = 0; i < eventObservers.size; i++) {
                eventObservers.get(i).onNotify(e, event);
            }
        }
    }

    public void addObserver(EntityEventObserver observer) {
        eventObservers.add(observer);
    }
}
