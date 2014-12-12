package com.ives.relative.entities.components.planet;


import com.artemis.Component;

/**
 * Created by Ives on 10/12/2014.
 */
public class Gravity extends Component {
    public float x;
    public float y;

    public Gravity() {
    }

    public Gravity(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
