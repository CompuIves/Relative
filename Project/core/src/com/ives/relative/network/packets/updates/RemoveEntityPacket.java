package com.ives.relative.network.packets.updates;

import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 18/12/2014.
 * Removes an entity from the world
 * <p/>
 * Handled by client
 */
public class RemoveEntityPacket extends ResponsePacket {
    int entityID;

    public RemoveEntityPacket() {
    }

    public RemoveEntityPacket(int entityID) {
        this.entityID = entityID;
    }

    @Override
    public void response(GameManager game) {
        game.world.getManager(NetworkManager.class).removeEntity(entityID);
    }
}
