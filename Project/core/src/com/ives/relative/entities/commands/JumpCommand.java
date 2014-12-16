package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;

/**
 * Created by Ives on 5/12/2014.
 */
public class JumpCommand extends Command {

    public JumpCommand() {
        super(true, false);
    }

    @Override
    public void executeDown(Entity entity, float delta) {
        Body body = entity.getWorld().getMapper(Physics.class).get(entity).body;
        if (body.getLinearVelocity().y == 0) {
            body.applyLinearImpulse(new Vector2(0, 10), body.getPosition(), true);
        }
    }

    @Override
    public void executeUp(Entity e) {

    }
}
