package com.ives.relative.entities.components.planet;


import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 10/12/2014.
 * Defines gravity of a planet.
 */
public class Gravity extends Component implements Networkable {
    public float x;
    public float y;

    public Gravity() {
    }

    public Gravity(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
