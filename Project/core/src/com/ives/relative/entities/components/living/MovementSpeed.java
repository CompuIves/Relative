package com.ives.relative.entities.components.living;


import com.artemis.Component;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSpeed extends Component {
    public float movementSpeed = 1f;

    public MovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public MovementSpeed() {
    }
}
