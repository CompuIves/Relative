package com.ives.relative.core.client;

import com.badlogic.gdx.Gdx;
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
 * A network handler for the client.
 */
public class ClientNetwork extends Network {
    public static int CONNECTIONID;
    public static int PLAYERNETWORKID;
    private static Client client;
    private ClientManager game;

    public ClientNetwork(ClientManager game, Client client) throws IOException {
        super(client);
        this.game = game;
        ClientNetwork.client = client;

        startClient();
        client.addListener(this);
    }

    private void startClient() throws IOException {
        client.start();
        client.connect(5000, "82.74.62.72", 54555, 54777);
    }

    @Override
    public void connected(Connection connection) {
        CONNECTIONID = connection.getID();
        //Send a connect packet with player name + version number + connection id
        ClientManager.PLAYERID = "Player" + MathUtils.random(0, 32);
        sendObjectTCP(CONNECTIONID, new ConnectPacket(Relative.VERSION, ClientManager.PLAYERID));
    }

    @Override
    public void disconnected(Connection connection) {
        Gdx.app.exit();
    }

    @Override
    public void closeConnection(int connection, final String message) {
        closeCurrentConnection(message);
        Gdx.app.exit();
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof ResponsePacket) {
            ((ResponsePacket) object).response(game);
        }
    }

    @Override
    public void sendObjectTCP(int connectionID, BasePacket o) {
        o.connection = connectionID;
        client.sendTCP(o);
    }

    /**
     * This SHOULD send a UDP packet, now it sends TCP
     *
     * @param connectionID connectionID to send to, which you can get with ClientNetwork.CONNECTIONID
     * @param o            packet to be sent
     */
    @Override
    public void sendObjectUDP(int connectionID, BasePacket o) {
        o.connection = connectionID;
        //TODO Write UDP Protocol
        client.sendUDP(o);
    }
}
