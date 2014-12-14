package com.ives.relative.core.packets.handshake;

import com.badlogic.gdx.Gdx;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.modules.GetNeededModules;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.managers.server.ServerPlayerManager;

/**
 * Created by Ives on 8/12/2014.
 * This packet will be sent by the client when it connects to the server
 * It contains the version of the game and the player id, if the version mismatches disconnect, if the player is already
 * in game, disconnect.
 * <p/>
 * On AcceptConnection send a request requesting the Modules
 * Handled from: SERVER
 */
public class ConnectPacket extends Packet {
    String version;
    String playerID;

    public ConnectPacket() {
    }

    public ConnectPacket(String version, String playerID) {
        this.version = version;
        this.playerID = playerID;
    }

    @Override
    public void response(GameManager game) {
        System.out.println("Got a connectpacket, checking for version and player...");
        if (Relative.VERSION.equals(version)) {
            //Check for players on the server
            //TODO watch for get(0)
            if (game.world.getManager(ServerPlayerManager.class).getEntitiesOfPlayer(playerID).get(0) != null) {
                System.out.println("Kicking client, player already connected.");
                connectionDenied(game.network, "Player already in the server");
            }


            //ACCEPTED SEND PLAYER
            this.connectionAccepted((ServerNetwork) game.network, game);
        } else {
            System.out.println("Kicking client, version mismatch.");
            connectionDenied(game.network, "Version mismatch (local: " + version + " remote: " + Relative.VERSION + ").");
        }
    }

    public void connectionDenied(Network network, final String message) {
        network.sendObjectTCP(connection, new DisconnectPacket("DISCONNECTING: " + message));
    }


    /**
     * If the connection is accepted it will add the entity to the client (it will be broadcasted by the networkengine automatically).
     * It will also send a packet asking for the list of modules the client has.
     *
     * @param network
     * @param game
     */
    public void connectionAccepted(final ServerNetwork network, final GameManager game) {
        System.out.println("Connection accepted!");
        System.out.println("Sending acceptedPacket and asking for ModulesNeeded");

        //Already add the player to the engine, don't send it to the user yet (modules need to be synchronized first)
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ServerPlayerManager serverPlayerManager = game.world.getManager(ServerPlayerManager.class);
                serverPlayerManager.addPlayerLoggingIn(connection, playerID);
                network.sendObjectTCP(connection, new GetNeededModules());
            }
        });
    }
}
