package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 4/12/2014.
 */
@Wire
public class WorldSystem extends IntervalEntitySystem {
    float interval;

    ComponentMapper<WorldComponent> worldMapper;

    public WorldSystem(float interval) {
        super(Aspect.getAspectForAll(WorldComponent.class), interval);
        this.interval = interval;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            WorldComponent worldComponent = worldMapper.get(entity);
            worldComponent.world.step(interval, worldComponent.velocityIterations, worldComponent.positionIterations);
        }
    }
}
