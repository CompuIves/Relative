package com.ives.relative.managers;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.network.networkentity.NetworkEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
public class NetworkManager extends Manager {
    Map<Long, Entity> networkEntities;
    Map<Entity, Long> networkIDs;

    long freeID;
    Array<Long> removedIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Long, Entity>();
        networkIDs = new HashMap<Entity, Long>();

        removedIDs = new Array<Long>();
    }

    public void setNetworkEntity(Entity e, NetworkEntity.Type type) {
        long id = getFreeID();
        e.edit().add(new NetworkC(id, type));
        setNetworkEntity(id, e);
    }

    public void setNetworkEntity(long id, Entity e) {
        networkEntities.put(id, e);
        networkIDs.put(e, id);
    }

    public long getFreeID() {
        if(removedIDs.size == 0) {
            freeID++;
            return freeID;
        } else {
            return removedIDs.first();
        }
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
        /*
        if (networkEntities.containsKey(id)) {
            //removeNetworkedEntity(id);
            mergeEntity(networkEntities.get(id), e);
        } else {
            setNetworkEntity(id, e);
            world.getEntityManager().added(e);
        }*/

        if (networkEntities.containsKey(id)) {
            removeNetworkedEntity(id);
        }
        setNetworkEntity(id, e);
        world.getEntityManager().added(e);
        //world.getEntityManager().added(e);
        //setNetworkEntity(id, e);
    }

    public void removeNetworkedEntity(long id) {
        Entity e = networkEntities.get(id);
        Physics physics = e.getWorld().getMapper(Physics.class).get(e);
        if (physics != null) {
            physics.body.getWorld().destroyBody(physics.body);
        }
        removedIDs.add(id);

        networkEntities.remove(id);
        networkIDs.remove(e);
        e.deleteFromWorld();
    }

    public void mergeEntity(Entity e1, Entity e2) {
        Bag<Component> componentse1 = new Bag<Component>();
        e1.getComponents(componentse1);
        Bag<Component> componentse2 = new Bag<Component>();
        e2.getComponents(componentse2);

        componentse1 = componentse2;
    }
}
