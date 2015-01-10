package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;

/**
 * Created by Ives on 10/12/2014.
 * Requests a beautiful world
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestPlanet extends ResponsePacket {
    String planetName;

    public RequestPlanet() {
    }

    public RequestPlanet(String planetName) {
        this.planetName = planetName;
    }

    @Override
    public void response(final GameManager game) {
        //TODO watch for several players
        //Entity player = game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection);
        //Entity planetName = game.world.getManager(PlanetManager.class).getPlanet(game.world.getMapper(Position.class).get(player).planetName);

        //TODO appropriate way of finding planetName
        Entity planet = game.world.getManager(PlanetManager.class).getPlanet(planetName);

        game.world.getSystem(NetworkSendSystem.class).sendEntity(connection, planet);
    }
}
