package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;

/**
 * Created by Ives on 9/1/2015.
 */
public class CollisionEvent extends EntityEvent {
    public boolean start;
    public Contact c;
    public Entity e1;
    public Entity e2;

    public CollisionEvent(boolean start, Contact c, Entity e1, Entity e2) {
        super(e1);
        this.start = start;
        this.c = c;
        this.e1 = e1;
        this.e2 = e2;
    }
}
