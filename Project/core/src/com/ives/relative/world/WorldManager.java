package com.ives.relative.world;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 12/1/2014.
 */
public class WorldManager {
    HashMap<String, Planet> planets;
    private float accumulator = 0;

    public static float TIME_STEP = 1/45f;
    public static int VELOCITY_ITERATIONS = 6;
    public static int POSITION_ITERATIONS = 2;

    public WorldManager() {
        planets = new HashMap<String, Planet>();
    }

    public HashMap<String, Planet> getPlanets() {
        return planets;
    }

    public Planet addPlanet(String id, Planet planet) {
        planets.put(id, planet);
        return planet;
    }

    public Planet getPlanet(String id) {
        return planets.get(id);
    }

    public void processSteps(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            for(Map.Entry entry : planets.entrySet()) {
                Planet planet = (Planet) entry.getValue();
                planet.timeStep(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            }
            accumulator -= TIME_STEP;
        }
    }
}
