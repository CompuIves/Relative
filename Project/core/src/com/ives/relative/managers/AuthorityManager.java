package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.JoinChunkEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.systems.server.ServerNetworkSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 6/1/2015.
 */
@Wire
public class AuthorityManager extends Manager implements EntityEventObserver {
    protected ServerPlayerManager serverPlayerManager;
    protected ServerNetworkSystem serverNetworkSystem;
    protected NetworkManager networkManager;
    protected UuidEntityManager uuidEntityManager;

    private Map<Integer, Array<Integer>> authorizedEntitiesByConnection;
    private Array<Integer> authorizedEntities;

    public AuthorityManager() {
        authorizedEntitiesByConnection = new HashMap<Integer, Array<Integer>>();
        authorizedEntities = new Array<Integer>();
    }

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {
        if (event instanceof JoinChunkEvent) {
            JoinChunkEvent joinChunkEvent = (JoinChunkEvent) event;
            if (serverPlayerManager.isPlayer(e)) {
                Array<Entity> entities = new Array<Entity>();
                for (UUID eID : joinChunkEvent.chunk.getEntities()) {
                    Entity entity = uuidEntityManager.getEntity(eID);
                    if (!isEntityAuthorized(entity)) {
                        entities.add(entity);
                    }
                }
                authorizeEntities(serverPlayerManager.getConnectionByPlayer(e), entities);
            }
        }
    }

    public boolean isEntityAuthorized(Entity e) {
        int networkID = networkManager.getNetworkID(e);
        return authorizedEntities.contains(networkID, true);
    }

    public void authorizeEntity(int connection, Entity entity) {
        int id = networkManager.getNetworkID(entity);
        if (id != -1) {
            authorizedEntities.add(id);

            getAuthorizedEntities(connection).add(id);
            serverNetworkSystem.sendAuthorization(connection, entity);
        }
    }

    public void authorizeEntities(int connection, Array<Entity> entities) {
        Array<Integer> authorizedConnectionEntities = getAuthorizedEntities(connection);
        Array<Integer> entityIDs = new Array<Integer>();
        for (Entity e : entities) {
            int id = networkManager.getNetworkID(e);
            if (id != -1) {
                authorizedEntities.add(id);
                authorizedConnectionEntities.add(id);
                entityIDs.add(id);
            }
        }
        System.out.println("Authorizing entities for connection: " + connection + " size: " + entityIDs.size);
        serverNetworkSystem.sendAuthorization(connection, entityIDs);
    }

    public Array<Integer> getAuthorizedEntities(int connection) {
        if (!authorizedEntitiesByConnection.containsKey(connection)) {
            authorizedEntitiesByConnection.put(connection, new Array<Integer>());
        }
        return authorizedEntitiesByConnection.get(connection);
    }

    public boolean isEntityAuthorizedByConnection(int connection, Entity e) {
        if (e != null) {
            return isEntityAuthorizedByConnection(connection, networkManager.getNetworkID(e));
        } else {
            return false;
        }
    }

    public boolean isEntityAuthorizedByConnection(int connection, int e) {
        if (e != -1) {
            Array<Integer> authorizedEntities = getAuthorizedEntities(connection);
            return authorizedEntities.contains(e, true);
        } else {
            return false;
        }
    }
}
