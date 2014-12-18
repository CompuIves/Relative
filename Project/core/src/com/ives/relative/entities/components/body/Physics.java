package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.ives.relative.entities.components.network.Networkable;
import com.ives.relative.network.serializers.box2d.FixtureDefSerializer;

/**
 * Created by Ives on 2/12/2014.
 */
public class Physics extends Component implements Networkable {
    @FieldSerializer.Bind(FixtureDefSerializer.class)
    public transient Body body = null;
    public transient Contact contact;

    public Physics() {
    }

    public Physics(Body body) {
        this.body = body;
    }
}
