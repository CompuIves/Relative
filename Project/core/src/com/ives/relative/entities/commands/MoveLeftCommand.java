package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.MovementSpeed;
import com.ives.relative.entities.components.body.Physics;

/**
 * Created by Ives on 5/12/2014.
 */
public class MoveLeftCommand extends Command {


    public MoveLeftCommand(boolean simulate) {
        super(simulate);
    }

    @Override
    public void execute(Entity e) {
        moveEntity(e, -e.getWorld().getMapper(MovementSpeed.class).get(e).movementSpeed);
    }

    private void moveEntity(Entity e, float x) {
        Body body = e.getWorld().getMapper(Physics.class).get(e).body;
        if (body.getLinearVelocity().x > x) {
            body.applyLinearImpulse(new Vector2(x, 0), new Vector2(body.getPosition().x, body.getPosition().y), true);
            System.out.println("BODYVX IS NOW " + body.getLinearVelocity().x);
        }
    }
}
