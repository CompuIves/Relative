package com.ives.relative.systems.server;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.handshake.planet.ChunkPacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.utils.ComponentUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ives on 7/1/2015.
 */
@Wire
public class NetworkSendSystem extends VoidEntitySystem {
    final LinkedBlockingQueue<Entity> entitiesToSendToAll;
    final LinkedHashMap<Integer, Entity> entitiesToSendToConnection;
    protected NetworkManager networkManager;
    protected UuidEntityManager uuidEntityManager;
    protected ComponentMapper<NetworkC> mNetworkC;
    private ServerNetwork serverNetwork;

    public NetworkSendSystem(ServerNetwork serverNetwork) {
        this.serverNetwork = serverNetwork;
        entitiesToSendToAll = new LinkedBlockingQueue<Entity>();
        entitiesToSendToConnection = new LinkedHashMap<Integer, Entity>();
    }

    //TODO see viability for system
    @Override
    protected void processSystem() {
        for (int i = 0; i < entitiesToSendToAll.size(); i++) {
            try {
                Entity e = entitiesToSendToAll.take();
                serverNetwork.sendObjectTCPToAll(generateFullComponentPacket(e));
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        Iterator it = entitiesToSendToConnection.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Entity> entry = (Map.Entry<Integer, Entity>) it.next();
            serverNetwork.sendObjectTCP(entry.getKey(), generateFullComponentPacket(entry.getValue()));
            it.remove();
        }
    }

    private void preProcessEntity(Entity e, Array<Component> components) {
        NetworkManager.Type type = mNetworkC.get(e).type;
        for (Component c : components) {
            if (c instanceof CustomNetworkComponent) {
                ((CustomNetworkComponent) c).convertForSending(e, world, type);
            }
        }
    }

    /**
     * This generates a full packet of the desired entity, this packet can be sent to the client or server and the client/
     * server will add this entity to it.
     *
     * @param e
     * @return
     */
    public CreateEntityPacket generateFullComponentPacket(Entity e) {
        Array<Component> components = ComponentUtils.getComponents(e);
        int id = networkManager.getNetworkID(e);
        preProcessEntity(e, components);
        //TODO fix sequence = -1
        return new CreateEntityPacket(components, id, false, -1);
    }

    public ChunkPacket generateFullChunkPacket(Chunk chunk) {
        List<CreateEntityPacket> entities = new ArrayList<CreateEntityPacket>(chunk.entities.size);
        for (UUID entity : chunk.entities) {
            Entity e = uuidEntityManager.getEntity(entity);
            entities.add(generateFullComponentPacket(e));
        }

        return new ChunkPacket(chunk.x, chunk.y, chunk.universeBody.id, entities, (HashMap<Vector2, Integer>) chunk.changedTiles);
    }

    public void sendEntityToAll(UUID entity) {
        sendEntityToAll(uuidEntityManager.getEntity(entity));
    }

    public void sendEntity(int connection, UUID entity) {
        sendEntity(connection, uuidEntityManager.getEntity(entity));
    }

    public void sendEntityToAll(Entity entity) {
        entitiesToSendToAll.add(entity);
    }

    public void sendEntity(int connection, Entity entity) {
        entitiesToSendToConnection.put(connection, entity);
    }
}
