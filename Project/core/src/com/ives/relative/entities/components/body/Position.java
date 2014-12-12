package com.ives.relative.entities.components.body;

import com.artemis.Component;

/**
 * Created by Ives on 12/12/2014.
 */
public class Position extends Component {
    public float x, y;
    public int z;
    public String worldID;

    public Position() {
    }

    public Position(float x, float y, int z, String worldID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldID = worldID;
    }
}
