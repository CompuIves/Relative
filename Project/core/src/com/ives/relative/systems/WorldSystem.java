package com.ives.relative.systems;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.systems.planet.GravitySystem;

/**
 * Created by Ives on 4/12/2014.
 * This system iterates the physics engine. Physics are simulated here!
 */
@Wire
public class WorldSystem extends VoidEntitySystem {
    public static final float PHYSICS_ITERATIONS = 1 / 60f;
    public static final int PHYSICS_VELOCITY_ITERATIONS = 10;
    public static final int PHYSICS_POSITION_ITERATIONS = 10;
    public com.badlogic.gdx.physics.box2d.World physicsWorld;
    protected GravitySystem gravitySystem;
    float accumulator;

    public WorldSystem(World physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    @Override
    protected void processSystem() {
        accumulator += world.getDelta();
        while (accumulator >= PHYSICS_ITERATIONS) {
            accumulator -= PHYSICS_ITERATIONS;
            gravitySystem.process();
            physicsWorld.step(PHYSICS_ITERATIONS, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
        }
    }
}
