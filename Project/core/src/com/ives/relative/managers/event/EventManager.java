package com.ives.relative.managers.event;

import com.artemis.Entity;
import com.artemis.Manager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 5/1/2015.
 */
public class EventManager extends Manager {
    protected final Array<EntityEventObserver> eventObservers;

    private final Map<String, EntityEvent> eventPool;

    public EventManager() {
        eventObservers = new Array<EntityEventObserver>();
        eventPool = new HashMap<String, EntityEvent>(20);
    }

    /**
     * Gets an EntityEvent from the pool, paramaters have to be set after the event has been received
     *
     * @param event  Class of the event requested
     * @param entity
     * @return EntityEvent with entity in it
     */
    public EntityEvent getEvent(Class<? extends EntityEvent> event, Entity entity) {
        EntityEvent entityEvent = null;
        if (eventPool.containsKey(event.getSimpleName())) {
            entityEvent = eventPool.get(event.getSimpleName());
            entityEvent.preReset();
        } else {
            try {
                entityEvent = event.newInstance();
                eventPool.put(entityEvent.type().getSimpleName(), entityEvent);
            } catch (InstantiationException e) {
                Gdx.app.error(toString(), "Couldn't create new event", e);
            } catch (IllegalAccessException e) {
                Gdx.app.error(toString(), "Couldn't create new event", e);
            }
        }
        if (entityEvent != null)
            entityEvent.entity = entity;
        return entityEvent;
    }

    /**
     * This notifies every interested observer of an event which happened to an entity
     *
     * @param event
     */
    public void notifyEvent(EntityEvent event) {
        for (int i = 0; i < eventObservers.size; i++) {
            eventObservers.get(i).onNotify(event);
        }
    }

    public void addObserver(EntityEventObserver observer) {
        eventObservers.add(observer);
    }
}
