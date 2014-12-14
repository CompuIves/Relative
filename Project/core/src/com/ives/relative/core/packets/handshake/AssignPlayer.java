package com.ives.relative.core.packets.handshake;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 13/12/2014.
 * Assigns the ID of the player which can be controlled to the player
 * <p/>
 * HANDLED BY CLIENT
 */
public class AssignPlayer extends Packet {
    long id;

    public AssignPlayer() {
    }

    public AssignPlayer(long id) {
        this.id = id;
    }

    @Override
    public void response(GameManager game) {
        ClientManager clientManager = (ClientManager) game;
        clientManager.playerNetworkID = id;

        game.network.sendObjectTCP(connection, new RequestWorldSnapshot());
    }
}
