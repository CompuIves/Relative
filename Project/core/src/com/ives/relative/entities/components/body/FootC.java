package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 3/1/2015.
 *
 * Component which functions as information bag for the sensor beneath living objects. The sensor checks if the
 * player is standing on the ground or not (useful for jump checks).
 */
public class FootC extends Component {
    public int contactAmount;
    public float yOffset;
    public transient Array<Contact> footContacts;
    public transient Array<Entity> standingOn;

    public FootC() {
        footContacts = new Array<Contact>();
        standingOn = new Array<Entity>();
    }
}
