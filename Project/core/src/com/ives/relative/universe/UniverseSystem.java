package com.ives.relative.universe;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.universe.chunks.builders.SquarePlanet;
import com.ives.relative.universe.planets.TileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 18/1/2015.
 *
 * System which iterates all the UniverseBodies. Also keeps track of every Space.
 */
@Wire
public class UniverseSystem extends VoidEntitySystem {
    public static final float ITERATIONS = 1 / 60f;
    private final String seed;

    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    //TODO create load system
    private final Map<String, Planet> spaces;

    private float accumulator;

    public UniverseSystem(String seed) {
        this.seed = seed;
        this.spaces = new HashMap<String, Planet>();
    }

    @Override
    protected void initialize() {
        createTemporaryGalaxy();
    }

    public Space getSpace(String id) {
        return spaces.get(id).getSpace();
    }


    @Override
    protected void processSystem() {
        accumulator += world.getDelta();
        while (accumulator >= ITERATIONS) {
            accumulator -= ITERATIONS;
            for (Planet planet : spaces.values()) {
                planet.getSpace().update();
            }
        }
    }


    public void createTemporaryGalaxy() {
        SolarSystem solarSystem = new SolarSystem();
        Planet.Builder planetBuilder = new Planet.Builder("ives", "ivesiscool", new SolarSystem(), 300, 300, 1000, 1000, new Vector2(0, -10));
        Planet earth = planetBuilder.build();
        earth.getSpace().setChunkBuilder(new SquarePlanet(earth.getSpace(), tileManager, uuidEntityManager, 20, earth.gravity));
        solarSystem.addPlanet(earth);
        spaces.put("ives", earth);
    }
}
