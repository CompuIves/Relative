package com.ives.relative.core.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.server.ServerPlayerManager;

/**
 * Created by Ives on 10/12/2014.
 * Requests a beautiful world
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestPlanetPacket extends Packet {

    public RequestPlanetPacket() {
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                //TODO watch for several players
                Entity player = game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection);
                //Entity planet = game.world.getManager(PlanetManager.class).getPlanet(game.world.getMapper(Position.class).get(player).worldID);

                //TODO appropriate way of finding planet
                Entity planet = game.world.getManager(PlanetManager.class).getPlanet("earth");

                ServerNetwork.getConnection(connection).sendTCP(new PlanetPacket(planet, game.world.getManager(NetworkManager.class).getNetworkID(planet)));
            }
        });
    }
}
