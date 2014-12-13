package com.ives.relative.core.packets.requests;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.updates.EntityAdd;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 13/12/2014.
 * Requests an entity
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestEntity extends Packet {
    long id;

    public RequestEntity() {
    }

    public RequestEntity(long id) {
        this.id = id;
    }

    @Override
    public void response(GameManager game) {
        game.network.sendObjectTCP(connection, new EntityAdd(game.world.getManager(NetworkManager.class).getNetworkEntity(id), 0, id));
    }
}
