package com.ives.relative.network.packets;

import com.ives.relative.core.GameManager;

/**
 * Created by Ives on 7/12/2014.
 * This packet requires an action to be made of the other side.
 */
public abstract class ResponsePacket extends BasePacket {
    /**
     * This is handled by the other side. If a client sends this packet the server will execute this and in reverse too.
     *
     * @param game The gamemanager is given because almost every packet needs it
     */
    public abstract void response(GameManager game);
}
