package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.ives.relative.entities.components.planet.WorldC;

/**
 * Created by Ives on 4/12/2014.
 */
@Wire
public class WorldSystem extends IntervalEntitySystem {
    public static float PHYSICS_ITERATIONS = 1 / 45f;

    ComponentMapper<WorldC> worldMapper;

    public WorldSystem(float interval) {
        super(Aspect.getAspectForAll(WorldC.class), interval);
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            WorldC worldC = worldMapper.get(entity);
            worldC.world.step(PHYSICS_ITERATIONS, worldC.velocityIterations, worldC.positionIterations);
        }
    }
}
