package com.ives.relative.core.packets;

import com.ives.relative.core.GameManager;

/**
 * Created by Ives on 8/12/2014.
 *
 * SERVER
 */
public class ToServerPositionPacket implements Packet {
    public float x, y;
    public float vx, vy;
    public String internalName;

    public ToServerPositionPacket() {
    }

    public ToServerPositionPacket(float x, float y, float vx, float vy, String internalName) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.internalName = internalName;
    }

    @Override
    public void handle(GameManager game) {
        game.proxy.network.sendObjectToAllUDP(new PositionPacket(x, y, vx, vy, internalName));
    }
}
