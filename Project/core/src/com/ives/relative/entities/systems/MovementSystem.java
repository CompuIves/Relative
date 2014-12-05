package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSystem extends IteratingSystem {

    public MovementSystem(Family family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
