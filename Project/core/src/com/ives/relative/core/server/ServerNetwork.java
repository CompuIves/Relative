package com.ives.relative.core.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.Packet;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerNetwork extends Network {
    public static Server server;
    ServerManager game;

    public ServerNetwork(ServerManager game, Server server) throws IOException {
        super(server, game);
        this.game = game;
        ServerNetwork.server = server;
        super.kryo = server.getKryo();
        game.registerKryoClasses(kryo);

        startServer();
        server.addListener(this);
    }

    public static Connection getConnection(int id) {
        for (Connection connection : server.getConnections()) {
            if (connection.getID() == id) {
                return connection;
            }
        }
        return null;
    }

    private void startServer() throws IOException{
        server.start();
        server.bind(54555, 54777);
    }

    @Override
    public void received(Connection connection, final Object object) {
        if(object instanceof Packet) {
            ((Packet) object).response(game);
        }
    }

    @Override
    public void connected(Connection connection) {

    }

    @Override
    public void closeConnection(int connection, final String message) {
        Gdx.app.log("ServerConnection", message);
        server.getConnections()[connection].close();
    }

    @Override
    public void sendObjectTCP(int connectionID, Packet o) {
        System.out.println("Sent a packet named: " + o.getClass().getSimpleName());
        server.sendToTCP(connectionID, o);
    }

    @Override
    public void sendObjectUDP(int connectionID, Packet o) {
        server.sendToUDP(connectionID, o);
    }

    public void sendObjectToAllTCP(Packet o) {
        server.sendToAllTCP(o);
    }

    public void sendObjectToAllUDP(Packet o) {
        server.sendToAllUDP(o);
    }
}
