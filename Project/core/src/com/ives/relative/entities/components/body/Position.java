package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 12/12/2014.
 * Position component, this component gets synced with the body component
 */
public class Position extends Component implements Networkable {
    public float x, y;
    public float px, py;

    public int z;
    public int pz;

    public float rotation;
    public float protation;

    public String planet;

    public Position() {
    }

    public Position(float x, float y, int z, float rotation, String worldID) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.planet = worldID;
    }
}
