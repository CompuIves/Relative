package com.ives.relative.core.packets.handshake.planet;

import com.badlogic.ashley.core.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.core.server.ServerProxy;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.systems.WorldSystem;

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
        Entity player = ServerProxy.players.get(connection);
        Entity planet = game.engine.getSystem(WorldSystem.class).getPlanet(Mappers.body.get(player).worldID);
        ServerNetwork.getConnection(connection).sendTCP(new PlanetPacket(planet));
    }
}
