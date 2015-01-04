package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.AskTransferSuccessful;

/**
 * Created by Ives on 10/12/2014.
 * Requests a beautiful world
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestPlanetPacket extends ResponsePacket {

    public RequestPlanetPacket() {
    }

    @Override
    public void response(final GameManager game) {
        //TODO watch for several players
        //Entity player = game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection);
        //Entity planet = game.world.getManager(PlanetManager.class).getPlanet(game.world.getMapper(Position.class).get(player).worldID);

        //TODO appropriate way of finding planet
        Entity planet = game.world.getManager(PlanetManager.class).getPlanet("earth");

        game.network.sendObjectTCP(connection, game.world.getManager(NetworkManager.class).generateFullComponentPacket(planet, NetworkManager.Type.PLANET));
        game.network.sendObjectTCP(connection, new AskTransferSuccessful());
    }
}
