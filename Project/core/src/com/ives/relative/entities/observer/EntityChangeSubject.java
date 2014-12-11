package com.ives.relative.entities.observer;

import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 11/12/2014.
 */
public class EntityChangeSubject {
    List<EntityChangeObserver> entityChangeObservers;

    public EntityChangeSubject() {
        entityChangeObservers = new ArrayList<EntityChangeObserver>();
    }

    public void addListener(EntityChangeObserver entityChangeObserver) {
        entityChangeObservers.add(entityChangeObserver);
    }

    public void removeListener(EntityChangeObserver entityChangeObserver) {
        entityChangeObservers.remove(entityChangeObserver);
    }

    public void notifyEntityChange(Entity entity, EntityChangeObserver.Event event) {
        for (EntityChangeObserver entityChangeObserver : entityChangeObservers) {
            entityChangeObserver.onEntityChange(entity, event);
        }
    }
}

