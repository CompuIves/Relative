package com.ives.relative.network.packets.handshake.planet;

import com.ives.relative.core.GameManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;

/**
 * Created by Ives on 11/1/2015.
 * Says to the server the planet is received and the server can send more info.
 * <p/>
 * HANDLED BY SERVER
 */
public class ReceivedPlanet extends ResponsePacket {
    public ReceivedPlanet() {
    }

    @Override
    public void response(GameManager game) {
        NetworkSendSystem networkSendSystem = game.world.getSystem(NetworkSendSystem.class);
        networkSendSystem.sendEntity(connection, game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection));
    }
}
