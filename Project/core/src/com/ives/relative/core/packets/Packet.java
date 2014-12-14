package com.ives.relative.core.packets;

import com.ives.relative.core.GameManager;

/**
 * Created by Ives on 7/12/2014.
 */
public abstract class Packet {
    public int connection;

    /**
     * This is handled by the other side. If a client sends this packet the server will execute this and in reverse too.
     *
     * @param game The gamemanager is given because almost every packet needs it
     */
    public abstract void response(GameManager game);
}
