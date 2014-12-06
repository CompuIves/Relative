package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Ives on 2/12/2014.
 */
public class BodyComponent extends Component {
    public Body body = null;

    public BodyComponent(Body body) {
        this.body = body;
    }
}
