package com.ives.relative.core.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.CreatePlanetPacket;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.PlayerPacket;
import com.ives.relative.core.packets.TilePacket;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerNetwork extends Network {
    GameManager game;
    public Server server;

    public ServerNetwork(GameManager game, Server server) {
        super(server);
        this.game = game;
        this.server = server;
        super.kryo = server.getKryo();
        game.registerKryoClasses(kryo);

        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.addListener(this);
    }

    private void startServer() throws IOException{
        server.start();
        server.bind(54555, 54777);
    }

    @Override
    public void sendObjectTCP(int connectionID, Packet o) {
        System.out.println("Sent a packet named: " + o.getClass().getSimpleName());
        server.sendToTCP(connectionID, o);
    }

    @Override
    public void received(Connection connection, final Object object) {
        if(object instanceof Packet) {
            ((Packet) object).handle(game);
        }
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    public void closeRemoteConnection(int connection, String message) {

    }

    @Override
    public void closeConnection(int connection, final String message) {
        Gdx.app.log("ServerConnection", message);
        server.getConnections()[connection].close();
    }

    @Override
    public void sendObjectToAllTCP(Packet o) {
        server.sendToAllTCP(o);
    }

    @Override
    public void sendObjectUDP(int connectionID, Packet o) {
        server.sendToUDP(connectionID, o);
    }

    @Override
    public void sendObjectToAllUDP(Packet o) {
        server.sendToAllUDP(o);
    }
}
