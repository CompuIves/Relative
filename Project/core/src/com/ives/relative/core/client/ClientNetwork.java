package com.ives.relative.core.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.ConnectPacket;
import com.ives.relative.core.packets.Packet;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    private GameManager game;
    private Client client;

    public Entity tempPlayer;

    public ClientNetwork(GameManager game, Client client) {
        super(client);
        this.game = game;
        this.client = client;

        super.kryo = client.getKryo();
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
        //Send a connect packet with player name + version number + connection id
        sendObjectTCP(connection.getID(), new ConnectPacket(Relative.VERSION, "Player" + MathUtils.random(0, 32), connection.getID()));

        //Send a packet requesting the modules
        //sendObjectTCP(connection.getID(), new RequestTilePacket(connection.getID()));

        //Send a packet requesting the planet
        //sendObjectTCP(connection.getID(), new RequestPlanetPacket(connection.getID()));
    }

    @Override
    public void closeConnection(int connection, final String message) {
        closeCurrentConnection(message);
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

    @Override
    public void sendObjectUDPtoServer(Packet o) {
        client.sendUDP(o);
    }

    @Override
    public void sendObjectToAllTCP(Packet o) {
        client.sendTCP(o);
    }

    @Override
    public void sendObjectUDP(int connectionID, Packet o) {

    }
}
