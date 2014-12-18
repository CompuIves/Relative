package com.ives.relative.network.packets.requests;

import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;

/**
 * Created by Ives on 13/12/2014.
 * Requests an entity
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestEntity extends ResponsePacket {
    long id;

    public RequestEntity() {
    }

    public RequestEntity(long id) {
        this.id = id;
    }

    @Override
    public void response(GameManager game) {
        game.network.sendObjectTCP(connection, new CreateEntityPacket(game.world.getManager(NetworkManager.class).getEntity(id), id));
    }
}
