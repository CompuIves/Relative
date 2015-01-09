package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.Networkable;

import java.util.UUID;

/**
 * Created by Ives on 2/12/2014.
 */
public class Physics extends Component implements Networkable {
    public transient Body body = null;
    public transient Array<Contact> contacts;
    public transient Array<UUID> entitiesInContact;
    public BodyDef.BodyType bodyType;

    public Physics() {
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();
    }

    public Physics(Body body, BodyDef.BodyType bodyType) {
        this.body = body;
        this.bodyType = bodyType;
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();
    }
}
