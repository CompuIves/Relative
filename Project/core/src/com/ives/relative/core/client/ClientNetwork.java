package com.ives.relative.core.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.Packet;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    private GameManager game;
    private Client client;
    private Kryo kryo;

    public ClientNetwork(GameManager game) {
        this.game = game;
        client = new Client();

        kryo = client.getKryo();
        game.registerKryoClasses(kryo);

        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addListener(this);
    }

    private void startClient() throws IOException {
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof Packet) {
            System.out.println("Received packet with type: " + object.getClass().getSimpleName());
            ((Packet) object).handle(game);
        }
    }

    @Override
    public void sendObjectTCP(int connectionID, Packet o) {
        client.sendTCP(o);
    }
}
