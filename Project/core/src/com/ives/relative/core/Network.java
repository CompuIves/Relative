package com.ives.relative.core;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    //Only for client
    public int connectionID;

    public void sendObjectTCP(int connectionID, Packet o) {}

    //Only for server
    public void sendObjectToAllTCP(Packet o) {}

    @Override
    public void connected(Connection connection) {
        connectionID = connection.getID();
    }
}
