package com.ives.relative.network.packets.handshake;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 13/12/2014.
 * Assigns the ID of the player which can be controlled to the player
 * <p/>
 * HANDLED BY CLIENT
 */
public class AssignPlayer extends ResponsePacket {
    long id;

    public AssignPlayer() {
    }

    public AssignPlayer(long id) {
        this.id = id;
    }

    @Override
    public void response(GameManager game) {
        game.world.getSystem(ClientNetworkSystem.class).registerPlayer(id);
        game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestWorldSnapshot());
    }
}
