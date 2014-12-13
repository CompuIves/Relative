package com.ives.relative.core.packets.handshake;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 8/12/2014.
 * This packet can go both ways, just a disconnectpacket.
 */
public class DisconnectPacket extends Packet {
    public String message;

    public DisconnectPacket() {
    }

    public DisconnectPacket(String message) {
        this.message = message;
    }

    @Override
    public void response(GameManager game) {
        game.network.closeConnection(connection, message);
    }
}
