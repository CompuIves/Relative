package com.ives.relative.systems.server;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.utils.ComponentUtils;

import java.util.UUID;

/**
 * Created by Ives on 7/1/2015.
 */
@Wire
public class NetworkSendSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;
    protected UuidEntityManager uuidEntityManager;

    protected ComponentMapper<NetworkC> mNetworkC;
    private ServerNetwork serverNetwork;

    public NetworkSendSystem(ServerNetwork serverNetwork) {
        this.serverNetwork = serverNetwork;
    }

    //TODO see viability for system
    @Override
    protected void processSystem() {

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
        //TODO fix sequence = -1
        return new CreateEntityPacket(components, id, false, -1);
    }

    public void sendEntityToAll(UUID entity) {
        sendEntityToAll(uuidEntityManager.getEntity(entity));
    }

    public void sendEntityToAll(Entity entity) {
        serverNetwork.sendObjectTCPToAll(generateFullComponentPacket(entity));
    }

    public void sendEntity(int connection, Entity entity) {
        serverNetwork.sendObjectTCP(connection, generateFullComponentPacket(entity));
    }
}
