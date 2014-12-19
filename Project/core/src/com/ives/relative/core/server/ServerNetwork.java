package com.ives.relative.core.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.ResponsePacket;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerNetwork extends Network {
    private static Server server;
    ServerManager game;

    public ServerNetwork(ServerManager game, Server server) throws IOException {
        super(server);
        this.game = game;
        ServerNetwork.server = server;
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

    private void startServer() throws IOException {
        server.start();
        server.bind(54555, 54777);
    }

    @Override
    public void received(Connection connection, final Object object) {
        if (object instanceof ResponsePacket) {
            System.out.println("SERVER: Received Packet: " + object.getClass().getSimpleName());
            ((ResponsePacket) object).response(game);
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
    public void sendObjectTCP(int connectionID, BasePacket o) {
        System.out.println("SERVER: Sent a packet named: " + o.getClass().getSimpleName());
        o.connection = connectionID;
        server.sendToTCP(connectionID, o);
    }

    @Override
    public void sendObjectUDP(int connectionID, BasePacket o) {
        server.sendToUDP(connectionID, o);
    }

    public void sendObjectTCPToAll(BasePacket o) {
        for (int connection : game.world.getManager(ServerPlayerManager.class).getConnections()) {
            server.sendToTCP(connection, o);
        }
    }

    public void sendObjectUDPToAll(BasePacket o) {
        for (int connection : game.world.getManager(ServerPlayerManager.class).getConnections()) {
            server.sendToUDP(connection, o);
        }
    }

    @Override
    public void disconnected(Connection connection) {
        /*
        ServerPlayerManager serverPlayerManager = game.world.getManager(ServerPlayerManager.class);
        int id = game.world.getManager(NetworkManager.class).getNetworkID(serverPlayerManager.getPlayerByConnection(connection.getID()));
        serverPlayerManager.removeConnection(connection.getID());
        sendObjectTCPToAll(new RemoveEntityPacket(id));
        game.world.getManager(NetworkManager.class).removeEntity(id);
        */
    }
}
