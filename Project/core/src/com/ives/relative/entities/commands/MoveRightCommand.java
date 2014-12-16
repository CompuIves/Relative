package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.MovementSpeed;
import com.ives.relative.entities.components.body.Physics;


/**
 * Created by Ives on 5/12/2014.
 */
public class MoveRightCommand extends Command {


    public MoveRightCommand() {
        super(true, true);
    }

    @Override
    public void executeDown(Entity e, float delta) {
        float mvSpeed = e.getWorld().getMapper(MovementSpeed.class).get(e).movementSpeed;
        float vx = mvSpeed;
        moveEntity(e, vx, delta);
    }

    @Override
    public void executeUp(Entity e) {

    }

    private void moveEntity(Entity e, float vx, float delta) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        float oldvx = body.getLinearVelocity().x;
        float deltavx = vx - oldvx;
        float impulse = body.getMass() * deltavx;
        body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
    }
}