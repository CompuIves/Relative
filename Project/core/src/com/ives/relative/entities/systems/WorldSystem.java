package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 4/12/2014.
 */
public class WorldSystem extends IntervalIteratingSystem {
    float interval;

    public WorldSystem(Family family, float interval) {
        super(family, interval);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        WorldComponent worldComponent = Mappers.world.get(entity);
        worldComponent.world.step(interval, worldComponent.velocityIterations, worldComponent.positionIterations);
    }

    public Entity getPlanet(String id) {
        Entity e = null;
        for(Entity entity : getEntities()) {
            if(Mappers.name.get(entity).internalName.equals(id)) {
                e = entity;
            }
        }
        return e;
    }

    public Entity getPlanet(World world) {
        Entity e = null;
        for (Entity entity : getEntities()) {
            if (Mappers.world.get(entity).world.equals(world)) {
                e = entity;
            }
        }
        return e;
    }
}
