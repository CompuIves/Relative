package com.ives.relative.core.packets.handshake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.modules.GetNeededModules;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 8/12/2014.
 * This packet will be sent by the client when it connects to the server
 * It contains the version of the game and the player id, if the version mismatches disconnect, if the player is already
 * in game, disconnect.
 *
 * On AcceptConnection send a request requesting the Modules
 * Handled from: SERVER
 */
public class ConnectPacket implements Packet {
    String version;
    String playerID;
    int connection;

    public ConnectPacket() {
    }

    public ConnectPacket(String version, String playerID, int connection) {
        this.version = version;
        this.playerID = playerID;
        this.connection = connection;
    }

    @Override
    public void response(GameManager game) {
        System.out.println("Got a connectpacket, checking for version and player...");
        if(Relative.VERSION.equals(version)) {
            //Check for players on the server
            //TODO watch for get(0)
            if (game.entityWorld.getManager(ServerPlayerManager.class).getEntitiesOfPlayer(playerID).get(0) != null) {
                System.out.println("Kicking client, player already connected.");
                connectionDenied(game.proxy.network, "Player already in the server");
            }


            //ACCEPTED SEND PLAYER
            this.connectionAccepted(game.proxy.network, game);
        } else {
            System.out.println("Kicking client, version mismatch.");
            connectionDenied(game.proxy.network, "Version mismatch (local: " + version + " remote: " + Relative.VERSION + ").");
        }
    }

    public void connectionDenied(Network network, final String message) {
        network.sendObjectTCP(connection, new DisconnectPacket("DISCONNECTING: " + message, connection));
    }


    /**
     * If the connection is accepted it will add the entity to the client (it will be broadcasted by the networkengine automatically).
     * It will also send a packet asking for the list of modules the client has.
     *
     * @param network
     * @param game
     */
    public void connectionAccepted(final Network network, final GameManager game) {
        System.out.println("Connection accepted!");
        System.out.println("Sending acceptedPacket and asking for ModulesNeeded");

        //Already add the player to the engine, don't send it to the user yet (modules need to be synchronized first)
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ServerPlayerManager serverPlayerManager = game.entityWorld.getManager(ServerPlayerManager.class);
                serverPlayerManager.createPlayer(connection, playerID, "Player", game.entityWorld.getManager(PlanetManager.class).getPlanet("earth"),
                        new Vector2(10, 10), 0);
            }
        });
        network.sendObjectTCP(connection, new GetNeededModules());
    }
}
