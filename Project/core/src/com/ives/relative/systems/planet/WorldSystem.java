package com.ives.relative.systems.planet;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.ives.relative.entities.components.planet.WorldC;

/**
 * Created by Ives on 4/12/2014.
 * This system iterates the physics engine. Physics are simulated here!
 */
@Wire
public class WorldSystem extends EntityProcessingSystem {
    public static float PHYSICS_ITERATIONS = 1 / 60f;
    protected GravitySystem gravitySystem;
    ComponentMapper<WorldC> worldMapper;

    public WorldSystem() {
        super(Aspect.getAspectForAll(WorldC.class));
    }

    @Override
    protected void process(Entity e) {
        WorldC worldC = worldMapper.get(e);

        worldC.acc += world.getDelta();
        while (worldC.acc >= PHYSICS_ITERATIONS) {
            worldC.acc -= PHYSICS_ITERATIONS;
            gravitySystem.process();
            worldC.world.step(PHYSICS_ITERATIONS, worldC.velocityIterations, worldC.positionIterations);
        }

    }
}