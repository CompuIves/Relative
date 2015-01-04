package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.components.body.FootC;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.managers.StateManager;

/**
 * Created by Ives on 5/12/2014.
 *
 * Makes the character JUMP!
 */
public class JumpCommand extends Command {
    float accumulator = 0;

    public JumpCommand() {
        super(true);
    }

    @Override
    public void executeDown(Entity e) {
    }

    /**
     * The jump method is in the execute part because the player maybe wants to jump multiple times with one tap.
     *
     * @param e The entity this will be executed on
     */
    @Override
    public void execute(Entity e, float delta) {
        float jumpingSpeed = 15;
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        body.applyLinearImpulse(new Vector2(0, jumpingSpeed), body.getPosition(), true);


        FootC footC = e.getWorld().getMapper(FootC.class).get(e);
        Array<Entity> standingOn = footC.standingOn;
        for (Entity eStanding : standingOn) {
            Body body2 = eStanding.getWorld().getMapper(Physics.class).get(eStanding).body;
            body2.applyLinearImpulse(new Vector2(-body.getLinearVelocity().x, -jumpingSpeed), new Vector2(body.getPosition().x, body.getPosition().y + footC.yOffset), true);
        }

        accumulator += delta;
        if (accumulator > 0.3f)
            accumulator = 0;
    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public void reset() {
        super.reset();
        accumulator = 0;
    }

    @Override
    public boolean canExecute(Entity e) {
        State s = e.getWorld().getMapper(State.class).get(e);
        return s.entityState != StateManager.EntityState.AIRBORNE && accumulator == 0;
    }

    @Override
    public Command clone() {
        return new JumpCommand();
    }
}
