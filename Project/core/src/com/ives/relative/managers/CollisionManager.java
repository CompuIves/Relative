package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.components.body.Physics;

/**
 * Created by Ives on 31/12/2014.
 * Checks collision of every entity and changes the PhysicsComponent accordingly
 */
@Wire
public class CollisionManager extends Manager implements ContactListener {

    protected StateManager stateManager;

    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<State> mState;

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

        if (!checkJumping(p1)) {
            stateManager.assertState(e1, StateManager.EntityState.STANDING);
        }

        if (!checkJumping(p2)) {
            stateManager.assertState(e2, StateManager.EntityState.STANDING);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        Physics p1 = mPhysics.get(e1);
        Physics p2 = mPhysics.get(e2);

        p1.contacts.removeValue(contact, false);
        p2.contacts.removeValue(contact, false);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private boolean checkJumping(Physics p) {
        Vector2 position = p.body.getPosition();
        for (Contact contact : p.contacts) {
            if (contact.isTouching()) {
                if (contact.getFixtureB().getBody().getPosition().y < position.y) {
                    return true;
                }
            }
        }
        return false;
    }
}
