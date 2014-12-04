package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Ives on 2/12/2014.
 */
public class VelocityComponent extends Component {
    Vector2 velocity;

    public VelocityComponent() {
        velocity = new Vector2();
    }

    public VelocityComponent(Vector2 velocity) {
        this.velocity = velocity;
    }
}
