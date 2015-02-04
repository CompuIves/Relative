package com.ives.relative.entities.events.position;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.ives.relative.entities.events.EntityEvent;

/**
 * Created by Ives on 9/1/2015.
 */
public class CollisionEvent extends EntityEvent {
    public boolean start;
    public Contact c;
    public Entity e1;
    public Entity e2;

    public CollisionEvent() {
    }

    @Override
    public void reset() {
        start = false;
        c = null;
        e1 = null;
        e2 = null;
    }
}
