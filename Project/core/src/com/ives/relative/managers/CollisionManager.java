package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.components.body.FootC;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.events.CollisionEvent;
import com.ives.relative.entities.events.ProximityAuthorityEvent;
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
    protected ComponentMapper<State> mState;
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
        checkProximityCollision(contact, true);
        eventManager.notifyEvent(e1, new CollisionEvent(true, contact, e1, e2));
        eventManager.notifyEvent(e2, new CollisionEvent(true, contact, e1, e2));
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
        checkProximityCollision(contact, false);
        eventManager.notifyEvent(e1, new CollisionEvent(false, contact, e1, e2));
        eventManager.notifyEvent(e2, new CollisionEvent(false, contact, e1, e2));
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

    private void checkProximityCollision(Contact contact, boolean start) {
        Entity permanent = null;
        Entity object = null;
        if (contact.getFixtureA().getUserData().equals(Authority.class)) {
            permanent = (Entity) contact.getFixtureA().getBody().getUserData();
            object = (Entity) contact.getFixtureB().getBody().getUserData();
        }

        if (contact.getFixtureB().getUserData().equals(Authority.class)) {
            permanent = (Entity) contact.getFixtureB().getBody().getUserData();
            object = (Entity) contact.getFixtureA().getBody().getUserData();
        }
        if (permanent != null && object != null) {
            if (mPhysics.get(object).bodyType == BodyDef.BodyType.DynamicBody) {
                if (permanent.getId() != object.getId()) {
                    eventManager.notifyEvent(permanent, new ProximityAuthorityEvent(permanent, object, start));
                }
            }
        }
    }
}
