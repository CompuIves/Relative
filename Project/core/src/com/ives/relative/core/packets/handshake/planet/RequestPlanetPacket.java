package com.ives.relative.core.packets.handshake.planet;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

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
        //Entity player = ServerProxy.players.get(connection);
        //Entity planet = game.engine.getSystem(WorldSystem.class).getPlanet(Mappers.body.get(player).worldID);
        //ServerNetwork.getConnection(connection).sendTCP(new PlanetPacket(planet));
    }
}
