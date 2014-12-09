package com.ives.relative.core;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    //Only for client
    public int connectionID;

    public EndPoint endPoint;
    public Kryo kryo;

    public Network(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

    public abstract void sendObjectTCP(int connectionID, Packet o);

    public void sendObjectTCPToServer(Packet o) {
    }

    //Only for server if client send just to server
    public void sendObjectToAllTCP(Packet o) {}

    public abstract void sendObjectUDP(int connectionID, Packet o);

    public void sendObjectUDPtoServer(Packet o) {}

    @Override
    public void connected(Connection connection) {
        connectionID = connection.getID();
    }

    public abstract void closeConnection(int connection, String message);

    public void closeCurrentConnection(String message) {
        Gdx.app.log("Connection", message);
        endPoint.close();
    }

    public void sendObjectToAllUDP(Packet packet) {
    }
}
