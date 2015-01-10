package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;

/**
 * Created by Ives on 10/12/2014.
 * Requests a beautiful world
 * Server creates the player and adds it to the world, then it sends the world. After that it sends the chunks
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestWorld extends ResponsePacket {

    public RequestWorld() {
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ServerPlayerManager serverPlayerManager = game.world.getManager(ServerPlayerManager.class);
                String playerID = serverPlayerManager.finishPlayerLoggingIn(connection);
                Entity player = serverPlayerManager.createPlayer(connection, playerID, "Player", game.world.getManager(PlanetManager.class).getPlanet("earth"),
                        new Vector2(5, 12), 0);

                //I'll have to do it this way since the object only gets sent next frame (so the world can update).
                Array<Integer> connections = serverPlayerManager.getConnections();
                for (int connection : connections) {
                    game.world.getSystem(NetworkSendSystem.class).sendEntity(connection, player);
                }

                //Finally adds the player to the list of connections.
                serverPlayerManager.addConnection(connection, player);

                Entity planet = game.world.getManager(PlanetManager.class).getPlanet(game.world.getMapper(Position.class).get(player).planet);
                game.world.getSystem(NetworkSendSystem.class).sendEntity(connection, planet);
            }
        });

    }
}
