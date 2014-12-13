package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.network.networkentity.NetworkEntity;
import com.ives.relative.entities.components.network.NetworkC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
public class NetworkManager extends Manager {
    Map<Long, Entity> networkEntities;
    Map<Entity, Long> networkIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Long, Entity>();
        networkIDs = new HashMap<Entity, Long>();
    }

    public void setNetworkEntity(Entity e, NetworkEntity.Type type) {
        long id = networkEntities.size();
        e.edit().add(new NetworkC(id, type));
        setNetworkEntity(id, e);
    }

    public void setNetworkEntity(long id, Entity e) {
        networkEntities.put(id, e);
        networkIDs.put(e, id);
    }

    public Entity getNetworkEntity(long id) {
        return networkEntities.get(id);
    }

    public long getNetworkID(Entity e) {
        return networkIDs.get(e);
    }

    /**
     * When a remote entity is added there is a chance of duplicates, this method looks for the id and edits the
     * existing entity accordingly.
     *
     * @param id id of the entity which needs to be changed
     * @param e  the new entity
     */
    public void updateEntity(long id, Entity e) {
        if (networkEntities.containsKey(id)) {
            removeNetworkedEntity(id);
        }

        world.getEntityManager().added(e);
        setNetworkEntity(id, e);
    }

    public void removeNetworkedEntity(long id) {
        Entity e = networkEntities.get(id);
        networkEntities.remove(id);
        networkIDs.remove(e);
        e.deleteFromWorld();
    }

    public Array<Entity> getNetworkEntities() {
        Array<Entity> entities = new Array<Entity>();
        for (Map.Entry entry : networkEntities.entrySet()) {
            entities.add((Entity) entry.getValue());
        }
        return entities;
    }

    public Array<Long> getNetworkIDs() {
        Array<Long> ids = new Array<Long>();
        for (Map.Entry entry : networkEntities.entrySet()) {
            ids.add((Long) entry.getKey());
        }
        return ids;
    }
}
