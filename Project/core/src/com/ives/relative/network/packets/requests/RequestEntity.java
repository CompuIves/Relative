package com.ives.relative.network.packets.requests;

import com.artemis.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.systems.server.NetworkSendSystem;

/**
 * Created by Ives on 13/12/2014.
 * Requests an entity
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestEntity extends ResponsePacket {
    int id;

    public RequestEntity() {
    }

    public RequestEntity(int id) {
        this.id = id;
    }

    @Override
    public void response(final GameManager game) {
        NetworkManager networkManager = game.world.getManager(NetworkManager.class);
        Entity entity = networkManager.getEntity(id);
        if (entity != null) {
            CreateEntityPacket createEntityPacket = game.world.getSystem(NetworkSendSystem.class).generateFullComponentPacket(networkManager.getEntity(id));
            game.network.sendObjectTCP(connection, createEntityPacket);
        }
    }
}
