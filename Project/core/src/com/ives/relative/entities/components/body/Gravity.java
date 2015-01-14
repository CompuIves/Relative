package com.ives.relative.entities.components.body;

import com.artemis.Component;

/**
 * Created by Ives on 14/1/2015.
 */
public class Gravity extends Component {
    public float gX;
    public float gY;

    public Gravity() {
    }

    public Gravity(float gX, float gY) {
        this.gX = gX;
        this.gY = gY;
    }
}
