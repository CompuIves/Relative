package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.living.MovementSpeed;


/**
 * Created by Ives on 5/12/2014.
 */
public class MoveRightCommand extends Command {

    public MoveRightCommand() {
        super(true);
    }

    @Override
    public void executeDown(Entity e) {
    }

    @Override
    public void execute(Entity e) {
        float mvSpeed = e.getComponent(MovementSpeed.class).movementSpeed;
        float vx = mvSpeed;
        moveEntity(e, vx);
    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    private void moveEntity(Entity e, float vx) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        float oldvx = body.getLinearVelocity().x;
        float deltavx = vx - oldvx;
        float impulse = body.getMass() * deltavx;
        body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
    }

    @Override
    public Command clone() {
        return new MoveRightCommand();
    }
}