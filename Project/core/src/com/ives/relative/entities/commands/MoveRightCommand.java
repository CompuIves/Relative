package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.VelocityComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class MoveRightCommand extends Command {
    @Override
    public void execute(Entity e) {
        moveEntity(e, Mappers.mvSpeed.get(e).movementSpeed);
    }

    private void moveEntity(Entity e, float x) {
        VelocityComponent velocityComponent = Mappers.velocity.get(e);
        velocityComponent.velocity.add(new Vector2(x, 0));
    }
}
