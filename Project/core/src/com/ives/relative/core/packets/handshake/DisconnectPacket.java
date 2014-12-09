package com.ives.relative.core.packets.handshake;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 8/12/2014.
 * This packet can go both ways, just a disconnectpacket.
 */
public class DisconnectPacket implements Packet {
    public String message;
    public int connection;

    public DisconnectPacket() {
    }

    public DisconnectPacket(String message, int connection) {
        this.message = message;
        this.connection = connection;
    }

    @Override
    public void handle(GameManager game) {
        game.proxy.network.closeConnection(connection, message);
    }
}
