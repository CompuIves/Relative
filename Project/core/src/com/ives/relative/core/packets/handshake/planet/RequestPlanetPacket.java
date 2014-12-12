package com.ives.relative.core.packets.handshake.planet;

import com.artemis.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.components.body.PhysicsPosition;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 10/12/2014.
 * Requests a beautiful world
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestPlanetPacket implements Packet {
    int connection;

    public RequestPlanetPacket() {

    }

    public RequestPlanetPacket(int connection) {
        this.connection = connection;
    }

    @Override
    public void response(GameManager game) {
        //TODO watch for several players
        Entity player = game.entityWorld.getManager(ServerPlayerManager.class).getPlayerByConnection(connection);
        Entity planet = game.entityWorld.getManager(PlanetManager.class).getPlanet(game.entityWorld.getMapper(PhysicsPosition.class).get(player).worldID);
        ServerNetwork.getConnection(connection).sendTCP(new PlanetPacket(planet));
    }
}
