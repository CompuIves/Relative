package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSpeedComponent extends Component {
    public float movementSpeed = 1f;

    public MovementSpeedComponent(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }
}
