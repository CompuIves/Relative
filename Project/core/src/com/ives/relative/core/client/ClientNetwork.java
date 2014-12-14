package com.ives.relative.core.client;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.Relative;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.ConnectPacket;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    public static int CONNECTIONID;
    private ClientManager game;
    private Client client;

    public ClientNetwork(ClientManager game, Client client) throws IOException {
        super(client);
        this.game = game;
        this.client = client;

        startClient();
        client.addListener(this);
    }

    private void startClient() throws IOException {
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);
    }

    @Override
    public void connected(Connection connection) {
        CONNECTIONID = connection.getID();
        //Send a connect packet with player name + version number + connection id
        game.playerID = "Player" + MathUtils.random(0, 32);
        sendObjectTCP(CONNECTIONID, new ConnectPacket(Relative.VERSION, game.playerID));

    }

    @Override
    public void closeConnection(int connection, final String message) {
        closeCurrentConnection(message);
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof ResponsePacket) {
            System.out.println("CLIENT: Received packet with type: " + object.getClass().getSimpleName());
            ((ResponsePacket) object).response(game);
        }
    }

    @Override
    public void sendObjectTCP(int connectionID, BasePacket o) {
        o.connection = connectionID;
        client.sendTCP(o);
    }

    @Override
    public void sendObjectUDP(int connectionID, BasePacket o) {
        client.sendTCP(o);
    }
}
