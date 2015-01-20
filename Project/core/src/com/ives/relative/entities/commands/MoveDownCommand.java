package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.living.MovementSpeed;
import com.ives.relative.utils.RelativePhysicsResolver;

/**
 * Created by Ives on 5/12/2014.
 */
public class MoveDownCommand extends Command {

    public MoveDownCommand() {
        super(true);
    }

    @Override
    public void executeDown(Entity e) {

    }

    @Override
    public void execute(Entity e, float delta) {
        //e.getWorld().getManager(StateManager.class).assertState(e, StateManager.EntityState.WALKING);
        float mvSpeed = e.getWorld().getMapper(MovementSpeed.class).get(e).movementSpeed;
        float vy = -mvSpeed;
        moveEntity(e, vy);
    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public boolean canExecute(Entity e) {
        State s = e.getWorld().getMapper(State.class).get(e);
        //return s.entityState != StateManager.EntityState.AIRBORNE; This gives much less flexibility
        return true;
    }

    private void moveEntity(Entity e, float vy) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        if (Math.abs(body.getLinearVelocity().y) > vy) {
            float impulse = body.getMass() * vy * 8;
            RelativePhysicsResolver.applyForce(0, impulse, body.getTransform().getRotation(), body);
        }
    }
}
