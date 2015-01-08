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
        p2.contacts.add(contact);

        checkHitGround(contact, true);
        checkProximityCollision(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        Physics p1 = mPhysics.get(e1);
        Physics p2 = mPhysics.get(e2);

        p1.contacts.removeValue(contact, false);
        p2.contacts.removeValue(contact, false);

        checkHitGround(contact, false);
        checkProximityCollision(contact, false);
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
        if (contact.isTouching()) {
            Entity permanent = null;
            Entity object = null;
            if (contact.getFixtureA().getUserData().equals(Authority.class)) {
                permanent = (Entity) contact.getFixtureA().getBody().getUserData();
                if (contact.getFixtureB().getBody().getType() == BodyDef.BodyType.DynamicBody)
                    object = (Entity) contact.getFixtureB().getBody().getUserData();
            }

            if (contact.getFixtureB().getUserData().equals(Authority.class)) {
                permanent = (Entity) contact.getFixtureB().getBody().getUserData();
                if (contact.getFixtureA().getBody().getType() == BodyDef.BodyType.DynamicBody)
                    object = (Entity) contact.getFixtureA().getBody().getUserData();
            }
            if (permanent != null && object != null) {
                if (permanent.getId() != object.getId()) {
                    eventManager.notifyEvent(permanent, new ProximityAuthorityEvent(permanent, object, start));
                }
            }
        }
    }
}
