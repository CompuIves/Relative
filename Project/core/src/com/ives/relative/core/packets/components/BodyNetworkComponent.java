package com.ives.relative.core.packets.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 5/12/2014.
 */
class BodyNetworkComponent extends Component{
    private float x;
    private float y;
    private int z;

    private float vX;
    private float vY;
    private String world;

    public BodyNetworkComponent(float x, float y, int z, float vX, float vY, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vX = vX;
        this.vY = vY;
        this.world = world;
    }
}
