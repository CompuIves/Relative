package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class MoveRightCommand extends Command {
    @Override
    public void execute(Entity e) {
        super.execute(e);
        moveEntity(e, Mappers.mvSpeed.get(e).movementSpeed);
    }

    private void moveEntity(Entity e, float x) {
        Body body = Mappers.body.get(e).body;
        float movementSpeed = Mappers.mvSpeed.get(e).movementSpeed;
        if(body.getLinearVelocity().x < movementSpeed) {
            body.applyLinearImpulse(new Vector2(x, 0), new Vector2(body.getPosition().x, body.getPosition().y), true);
        }
    }
}
