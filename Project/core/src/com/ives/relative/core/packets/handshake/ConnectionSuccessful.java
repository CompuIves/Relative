package com.ives.relative.core.packets.handshake;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.server.ServerPlayerManager;

/**
 * Created by Ives on 13/12/2014.
 * This packet will notice the server of a successful connection, the server will now add the client to the queue of world
 * updates. And send which player the client can control.
 * <p/>
 * HANDLED BY SERVER
 */
public class ConnectionSuccessful extends Packet {
    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ServerPlayerManager serverPlayerManager = game.world.getManager(ServerPlayerManager.class);
                String playerID = serverPlayerManager.finishPlayerLoggingIn(connection);
                Entity player = serverPlayerManager.createPlayer(playerID, "Player", game.world.getManager(PlanetManager.class).getPlanet("earth"),
                        new Vector2(10, 10), 0);
                serverPlayerManager.addConnection(connection, player);

                game.network.sendObjectTCP(connection, new AssignPlayer(game.world.getManager(NetworkManager.class).getNetworkID(player)));
            }
        });
    }
}
