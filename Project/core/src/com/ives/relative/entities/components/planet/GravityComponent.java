package com.ives.relative.entities.components.planet;


import com.artemis.Component;

/**
 * Created by Ives on 10/12/2014.
 */
public class GravityComponent extends Component {
    public float gravityX;
    public float gravityY;

    public GravityComponent() {
    }

    public GravityComponent(float gravityX, float gravityY) {
        this.gravityX = gravityX;
        this.gravityY = gravityY;
    }

}
