package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class JumpCommand extends Command {
    @Override
    public void execute(Entity entity) {
        Body body = Mappers.body.get(entity).body;
        if (body.getLinearVelocity().y == 0) {
            body.applyLinearImpulse(new Vector2(0, 40), body.getPosition(), true);
        }
    }
}
