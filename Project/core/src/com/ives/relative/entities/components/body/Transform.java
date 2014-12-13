package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 12/12/2014.
 */
public class Transform extends Component {
    public float width, height;
    public transient Array<Fixture> fixtures;

    public Transform() {
    }

    public Transform(float width, float height, Array<Fixture> fixtures) {
        this.width = width;
        this.height = height;
        this.fixtures = fixtures;
    }
}
