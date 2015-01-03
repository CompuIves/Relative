package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 3/1/2015.
 */
public class FootC extends Component {
    public int contactAmount;
    public Array<Contact> footContacts;

    public FootC() {
        footContacts = new Array<Contact>();
    }
}
