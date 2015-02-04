package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ives.relative.entities.components.body.FootC;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.events.position.CollisionEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.event.StateManager;

/**
 * Created by Ives on 31/12/2014.
 * Checks collision of every entity and changes the PhysicsComponent accordingly
 */
@Wire
public class CollisionManager extends Manager implements ContactListener {

    protected StateManager stateManager;
    protected EventManager eventManager;

    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<FootC> mFootC;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public CollisionManager() {
    }

    @Override
    public void beginContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        Physics p1 = mPhysics.get(e1);
        Physics p2 = mPhysics.get(e2);

        p1.contacts.add(contact);
        p1.entitiesInContact.add(e2.getUuid());
        p2.contacts.add(contact);
        p2.entitiesInContact.add(e1.getUuid());

        checkHitGround(contact, true);

        CollisionEvent collisionEvent = (CollisionEvent) eventManager.getEvent(CollisionEvent.class, e1);
        collisionEvent.start = true;
        collisionEvent.c = contact;
        collisionEvent.e1 = e1;
        collisionEvent.e2 = e2;
    }

    @Override
    public void endContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        Physics p1 = mPhysics.get(e1);
        Physics p2 = mPhysics.get(e2);

        p1.contacts.removeValue(contact, false);
        p1.entitiesInContact.removeValue(e2.getUuid(), true);
        p2.contacts.removeValue(contact, false);
        p2.entitiesInContact.removeValue(e1.getUuid(), true);

        checkHitGround(contact, false);
        //Maybe I have to call CollisionEvent two times
        CollisionEvent collisionEvent = (CollisionEvent) eventManager.getEvent(CollisionEvent.class, e1);
        collisionEvent.start = false;
        collisionEvent.c = contact;
        collisionEvent.e1 = e1;
        collisionEvent.e2 = e2;
        eventManager.notifyEvent(collisionEvent);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void checkHitGround(Contact contact, boolean beginContact) {
        Entity e = null;
        Entity eStanding = null;
        if (contact.getFixtureA().getUserData().equals(FootC.class)) {
            e = (Entity) contact.getFixtureA().getBody().getUserData();
            eStanding = (Entity) contact.getFixtureB().getBody().getUserData();
        }
        if (contact.getFixtureB().getUserData().equals(FootC.class)) {
            e = (Entity) contact.getFixtureB().getBody().getUserData();
            eStanding = (Entity) contact.getFixtureA().getBody().getUserData();
        }

        if (e != null && eStanding != null) {
            if (e.getId() != eStanding.getId()) {
                if (beginContact) {
                    addFootC(contact, e, eStanding);
                } else {
                    removeFootC(contact, e, eStanding);
                }
            }
        }
    }

    private void addFootC(Contact contact, Entity e, Entity eStanding) {
        FootC footC = mFootC.get(e);
        footC.footContacts.add(contact);
        footC.standingOn.add(eStanding);
        footC.contactAmount++;
        if (footC.contactAmount > 0) {
            stateManager.assertState(e, StateManager.EntityState.STANDING);
        }
    }

    private void removeFootC(Contact contact, Entity e, Entity eStanding) {
        FootC footC = mFootC.get(e);
        footC.footContacts.removeValue(contact, false);
        footC.standingOn.removeValue(eStanding, false);
        footC.contactAmount--;
        if (footC.contactAmount == 0) {
            stateManager.assertState(e, StateManager.EntityState.AIRBORNE);
        }
    }
}
