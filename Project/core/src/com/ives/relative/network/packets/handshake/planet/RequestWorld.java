package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.universe.UniverseSystem;

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
                Entity player = serverPlayerManager.createPlayer(connection, playerID, "Player",
                        game.world.getSystem(UniverseSystem.class).getUniverseBody("ives"), new Vector2(0, 500), 0);

                //Finally adds the player to the list of connections.
                serverPlayerManager.addConnection(connection, player);

                game.world.getSystem(NetworkSendSystem.class).sendEntityToAll(player);
            }
        });

    }
}
