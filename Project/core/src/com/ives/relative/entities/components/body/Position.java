package com.ives.relative.entities.components.body;

import com.artemis.Component;

/**
 * Created by Ives on 12/12/2014.
 * Position component, this component gets synced with the body component
 */
public class Position extends Component {
    public float x, y;
    public int z;
    public float rotation;
    public String worldID;

    public Position() {
    }

    public Position(float x, float y, int z, float rotation, String worldID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.worldID = worldID;
    }
}
