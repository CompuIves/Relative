package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 13/12/2014.
 * This keeps the database of every networked entity and their network ID. It also processes incoming entities
 */
@Wire
public class NetworkManager extends Manager {
    protected ComponentMapper<NetworkC> mNetworkC;
    protected UuidEntityManager uuidEntityManager;
    protected ChunkManager chunkManager;
    Map<Integer, UUID> networkEntities;
    Map<UUID, Integer> networkIDs;
    int freeID;
    Array<Integer> removedIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Integer, UUID>();
        networkIDs = new HashMap<UUID, Integer>();

        removedIDs = new Array<Integer>();
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
            chunkManager.addEntity(e);
        }
    }

    /**
     * Remove networked entity from world and database
     * @param id id of networked entity
     */
    public void removeEntity(int id) {
        Entity e = uuidEntityManager.getEntity(networkEntities.get(id));
        if (e != null) {
            ComponentUtils.removeAllSpecialComponents(e);
            removedIDs.add(id);

            networkEntities.remove(id);
            networkIDs.remove(uuidEntityManager.getUuid(e));

            chunkManager.removeEntity(e);
            System.out.println("Removed entity: " + e.getId());
            e.deleteFromWorld();
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

    public enum Type {
        PLAYER,
        TILE,
        PLANET, OTHER
    }
}
