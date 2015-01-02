package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.State;
import com.ives.relative.utils.EntityChangeObserver;

/**
 * Created by Ives on 2/1/2015.
 * This manager makes sure that every system or manager interested in state changes of entities are notified when an
 * entity changes. This could be useful for things like notifying the server an entity has changed and needs to be
 * sent to client.
 */
@Wire
public class StateManager extends Manager {
    protected final Array<EntityChangeObserver> observers;
    protected ComponentMapper<State> mState;

    public StateManager() {
        observers = new Array<EntityChangeObserver>();
    }

    public void assertState(Entity e, EntityState state) {
        State stateC = e.getComponent(State.class);
        if (stateC != null) {
            stateC.entityState = state;
            notifyObservers(e, state);
        }
    }

    public void addObserver(EntityChangeObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Entity e, EntityState state) {
        for (EntityChangeObserver observer : observers) {
            observer.onNotify(e, state);
        }
    }

    public enum EntityState {
        WALKING,
        STANDING,
        JUMPING
    }
}
