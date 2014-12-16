package com.ives.relative.network;

/**
 * Created by Ives on 15/12/2014.
 */
public class ClientMove {
    public float dx, dy;
    public float dvx, dvy;

    public ClientMove(float dx, float dy, float dvx, float dvy) {
        this.dx = dx;
        this.dy = dy;
        this.dvx = dvx;
        this.dvy = dvy;
    }
}
