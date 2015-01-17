package com.ives.relative.entities.components.planet;

import com.artemis.Component;

/**
 * Created by Ives on 17/1/2015.
 * <p/>
 * Position of a planet in the universe!
 */
public class PPosition extends Component {
    public float x, y;

    public PPosition() {
    }

    public PPosition(float x, float y) {

        this.x = x;
        this.y = y;
    }
}
