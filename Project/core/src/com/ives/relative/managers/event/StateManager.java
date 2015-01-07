package com.ives.relative.managers.event;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.events.EntityChangeObserver;

/**
 * Created by Ives on 2/1/2015.
 * This manager makes sure that every system or manager interested in state changes of entities are notified when an
 * entity changes. This could be useful for things like notifying the server an entity has changed and needs to be
 * sent to client.
 */
@Wire
public class StateManager extends Manager {
    protected final Array<EntityChangeObserver> stateObservers;
    protected ComponentMapper<State> mState;

    public StateManager() {
        stateObservers = new Array<EntityChangeObserver>();
    }

    public void assertState(Entity e, EntityState state) {
        if (mState.has(e)) {
            State stateC = mState.get(e);
            stateC.entityState = state;
            notifyObservers(e, state);
        }
    }

    public void addObserver(EntityChangeObserver observer) {
        stateObservers.add(observer);
    }

    public void notifyObservers(Entity e, EntityState state) {
        for (EntityChangeObserver observer : stateObservers) {
            observer.onNotify(e, state);
        }
    }

    public enum EntityState {
        WALKING,
        STANDING,
        AIRBORNE
    }
}
