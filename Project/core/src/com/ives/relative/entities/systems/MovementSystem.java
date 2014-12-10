package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.systems.network.ServerNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public class MovementSystem extends IteratingSystem {
    Engine engine;

    public MovementSystem(Family family, Engine engine) {
        super(family);
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!Mappers.body.get(entity).body.getLinearVelocity().isZero()) {
            engine.getSystem(ServerNetworkSystem.class).addEvent();
        }
    }
}
