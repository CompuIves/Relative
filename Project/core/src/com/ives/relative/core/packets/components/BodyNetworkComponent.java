package com.ives.relative.core.packets.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 5/12/2014.
 */
public class BodyNetworkComponent extends Component{
    public float x, y;
    public int z;

    public float vX, vY;
    public String world;

    public BodyNetworkComponent(float x, float y, int z, float vX, float vY, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vX = vX;
        this.vY = vY;
        this.world = world;
    }
}
