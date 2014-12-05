package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ives.relative.entities.components.VelocityComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSystem extends IteratingSystem {

    public MovementSystem(Family family) {
        super(family);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocityComponent = Mappers.velocity.get(entity);
        if(velocityComponent.velocity )
    }
}
