package com.ives.relative.core.client;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.ConnectPacket;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    public static int CONNECTIONID;
    private GameManager game;
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
        sendObjectTCP(CONNECTIONID, new ConnectPacket(Relative.VERSION, "Player" + MathUtils.random(0, 32)));
    }

    @Override
    public void closeConnection(int connection, final String message) {
        closeCurrentConnection(message);
    }

    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof Packet) {
            System.out.println("Received packet with type: " + object.getClass().getSimpleName());
            ((Packet) object).response(game);
        }
    }

    @Override
    public void sendObjectTCP(int connectionID, Packet o) {
        o.connection = connectionID;
        client.sendTCP(o);
    }

    @Override
    public void sendObjectUDP(int connectionID, Packet o) {
        client.sendTCP(o);
    }
}
