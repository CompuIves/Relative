package com.ives.relative.universe;

import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.systems.planet.GravitySystem;
import com.ives.relative.universe.chunks.builders.SquarePlanet;
import com.ives.relative.universe.planets.TileManager;

import java.util.HashMap;

/**
 * Created by Ives on 18/1/2015.
 *
 * System which iterates all the UniverseBodies. Also keeps track of every UniverseBody.
 */
@Wire
public class UniverseSystem extends VoidEntitySystem {
    public static final float ITERATIONS = 1 / 60f;
    private final Array<UniverseBody> galaxies;
    private final HashMap<String, UniverseBody> universeBodiesByID;
    private final String seed;

    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;
    protected GravitySystem gravitySystem;
    private float accumulator;

    public UniverseSystem(String seed) {
        this.seed = seed;
        galaxies = new Array<UniverseBody>();
        universeBodiesByID = new HashMap<String, UniverseBody>();
    }

    @Override
    protected void initialize() {
        createTemporaryGalaxy();
    }

    @Override
    protected void processSystem() {
        accumulator += world.getDelta();
        while (accumulator >= ITERATIONS) {
            accumulator -= ITERATIONS;
            gravitySystem.process();
            for (UniverseBody universeBody : galaxies) {
                universeBody.update();
            }
        }
    }

    /**
     * Get a universebody by ID.
     *
     * @param id id of the universebody
     * @return return the universebody
     */
    public UniverseBody getUniverseBody(String id) {
        return universeBodiesByID.get(id);
    }

    public void createTemporaryGalaxy() {
        CollisionManager collisionManager = world.getManager(CollisionManager.class);

        //Create a simple galaxy before generating this is handled
        UniverseBody galaxy = new UniverseBody(collisionManager, "andromeda", null, 0, 0, 100000, 100000, 0, new Vector2(1, 1), 512);
        universeBodiesByID.put("andromeda", galaxy);
        galaxies.add(galaxy);

        UniverseBody starSystem = new UniverseBody(collisionManager, "starsystem101", galaxy, 0, 0, 50000, 50000, 0, new Vector2(1, 1), 256);
        universeBodiesByID.put("starsystem101", starSystem);

        UniverseBody solarSystem = new UniverseBody(collisionManager, "ivesolaria", starSystem, 0, 0, 10000, 10000, 0, new Vector2(1, 1), 32);
        universeBodiesByID.put("ivesolaria", solarSystem);

        Planet.Builder planetBuilder = new Planet.Builder("ives", "ivesiscool", solarSystem, 300, 300, 140, 140, new Vector2(-10, 0), collisionManager);
        Planet earth = planetBuilder.build();
        universeBodiesByID.put("ives", earth);
        earth.setChunkBuilder(new SquarePlanet(earth, tileManager, uuidEntityManager, earth.gravity));
    }
}
