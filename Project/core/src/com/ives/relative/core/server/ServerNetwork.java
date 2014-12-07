package com.ives.relative.core.server;

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
    Server server;
    Kryo kryo;

    public ServerNetwork(GameManager game) {
        this.game = game;

        this.server = new Server();
        kryo = server.getKryo();
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
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
        loginClient(connection);
    }

    @Override
    public void sendObjectToAllTCP(Packet o) {
        server.sendToAllTCP(o);
    }

    private void loginClient(Connection connection) {
        sendObjectToAllTCP(new PlayerPacket("player" + MathUtils.random(0, 32), "Player", "earth", 10, 10, 0, connection.getID()));

        for(Map.Entry entry : game.tileManager.solidTiles.entrySet()) {
            SolidTile tile = (SolidTile) entry.getValue();
            sendObjectTCP(connection.getID(), new TilePacket(tile));
        }
        sendObjectTCP(connection.getID(), new CreatePlanetPacket());
    }


}
