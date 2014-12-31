package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 2/12/2014.
 */
public class Physics extends Component implements Networkable {
    public transient Body body = null;
    public transient Array<Contact> contacts;

    public Physics() {
        contacts = new Array<Contact>();
    }

    public Physics(Body body) {
        this.body = body;
        contacts = new Array<Contact>();
    }
}
