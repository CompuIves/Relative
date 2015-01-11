package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.creation.EntityDeletionEvent;
import com.ives.relative.entities.events.creation.NetworkedEntityCreationEvent;
import com.ives.relative.entities.events.creation.NetworkedEntityDeletionEvent;
import com.ives.relative.managers.event.EventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 13/12/2014.
 * This keeps the database of every networked entity and their network ID. It also processes incoming entities
 */
@Wire
public class NetworkManager extends Manager implements EntityEventObserver {
    protected UuidEntityManager uuidEntityManager;
    protected EventManager eventManager;
    Map<Integer, UUID> networkEntities;
    Map<UUID, Integer> networkIDs;
    int freeID;
    Array<Integer> removedIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Integer, UUID>();
        networkIDs = new HashMap<UUID, Integer>();

        removedIDs = new Array<Integer>();
    }

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    /**
     * Adds this entity with new id
     *
     * @param e entity to be added
     * @return the id it refers to
     */
    public int addEntity(Entity e) {
        int id = getFreeID();
        addEntity(id, e);
        return id;
    }

    /**
     * Add an entity to the networkmanager.
     *
     * @param id
     * @param e
     */
    public void addEntity(int id, Entity e) {
        if (!networkEntities.containsKey(id) && !networkIDs.containsKey(uuidEntityManager.getUuid(e))) {
            networkEntities.put(id, uuidEntityManager.getUuid(e));
            networkIDs.put(uuidEntityManager.getUuid(e), id);

            eventManager.notifyEvent(new NetworkedEntityCreationEvent(e));
        }
    }

    /**
     * Remove networked entity from world and database
     * @param id id of networked entity
     */
    public void removeEntity(int id) {
        Entity e = uuidEntityManager.getEntity(networkEntities.get(id));
        if (e != null) {
            removedIDs.add(id);
            networkEntities.remove(id);
            networkIDs.remove(uuidEntityManager.getUuid(e));

            eventManager.notifyEvent(new NetworkedEntityDeletionEvent(e));
        }
    }

    public Entity getEntity(int id) {
        if (networkEntities.containsKey(id)) {
            return uuidEntityManager.getEntity(networkEntities.get(id));
        } else {
            return null;
        }
    }

    public boolean containsEntity(int id) {
        return networkEntities.containsKey(id);
    }

    /**
     * Get the network ID of the entity
     * @param e
     * @return
     */
    public int getNetworkID(Entity e) {
        if (e != null && networkIDs.containsKey(uuidEntityManager.getUuid(e)))
            return networkIDs.get(uuidEntityManager.getUuid(e));
        else
            return -1;
    }

    /**
     * Gets free ID available, this only gets executed from the server
     * @return free ID
     */
    private int getFreeID() {
        if (removedIDs.size == 0) {
            freeID++;
            return freeID;
        } else {
            return removedIDs.first();
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof EntityDeletionEvent) {
            int id;
            if ((id = getNetworkID(event.entity)) != -1) {
                removeEntity(id);
            }
        }
    }

    public enum Type {
        PLAYER,
        TILE,
        PLANET, OTHER
    }
}
