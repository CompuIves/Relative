package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 12/12/2014.
 */
public class Transform extends Component implements Networkable {
    public float width, height;
    public Array<FixtureDef> fixtures;

    public Transform() {
    }

    public Transform(float width, float height, Array<FixtureDef> fixtures) {
        this.width = width;
        this.height = height;
        this.fixtures = fixtures;
    }
}
