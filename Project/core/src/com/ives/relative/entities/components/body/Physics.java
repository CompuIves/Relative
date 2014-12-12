package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Ives on 2/12/2014.
 */
public class Physics extends Component {
    public transient Body body = null;

    public Physics() {
    }

    public Physics(Body body) {
        this.body = body;
    }
}
