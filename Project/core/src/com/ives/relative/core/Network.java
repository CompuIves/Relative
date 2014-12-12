package com.ives.relative.core;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.packets.Packet;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    public EndPoint endPoint;
    public Kryo kryo;

    public Network(EndPoint endPoint, GameManager gameManager) throws IOException {
        this.endPoint = endPoint;
        kryo = endPoint.getKryo();
        gameManager.addKryoClasses();
        gameManager.registerKryoClasses(kryo);
    }

    public abstract void sendObjectTCP(int connectionID, Packet o);

    public abstract void sendObjectUDP(int connectionID, Packet o);

    @Override
    public abstract void connected(Connection connection);

    public abstract void closeConnection(int connection, String message);

    public void closeCurrentConnection(String message) {
        Gdx.app.log("Connection", message);
        endPoint.close();
    }
}
