package com.ives.relative.entities.components.living;


import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSpeed extends Component implements Networkable {
    public float movementSpeed;

    public MovementSpeed() {
    }

    public MovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
