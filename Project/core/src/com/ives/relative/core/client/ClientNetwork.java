package com.ives.relative.core.client;

import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.EntityPacket;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    GameManager game;
    Client client;
    Kryo kryo;

    public ClientNetwork(GameManager game) {
        this.game = game;

        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        kryo = client.getKryo();
        game.registerKryoClasses(kryo);

        client.addListener(this);
    }

    private void startClient() throws IOException {
        client = new Client();
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);
    }

    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof EntityPacket) {
            EntityPacket e = (EntityPacket) object;
            System.out.println("Received entity with id: " + e.message);
        }
    }
}
