package com.ives.relative.universe;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.builders.SquarePlanet;
import com.ives.relative.universe.planets.TileManager;

import java.util.HashMap;

/**
 * Created by Ives on 18/1/2015.
 */
@Wire
public class UniverseManager extends Manager {
    private final Array<UniverseBody> galaxies;
    private final HashMap<String, UniverseBody> universeBodiesByID;
    private final String seed;
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public UniverseManager(String seed) {
        this.seed = seed;
        galaxies = new Array<UniverseBody>();
        universeBodiesByID = new HashMap<String, UniverseBody>();
    }

    @Override
    protected void initialize() {
        createTemporaryGalaxy();
    }

    public UniverseBody getGalaxy(float x, float y) {
        for (UniverseBody galaxy : galaxies) {
            if (galaxy.isInBody(new Vector2(x, y))) {
                return galaxy;
            }
        }
        return null;
    }

    public UniverseBody findHighestUniverseBody(float x, float y) {
        UniverseBody galaxy = getGalaxy(x, y);
        if (galaxy != null) {
            return galaxy.getLowestChild(new Vector2(x, y), false);
        } else {
            return null;
        }
    }

    /**
     * Get an universebody by ID.
     *
     * @param id id of the universebody
     * @return return the universebody
     */
    public UniverseBody getUniverseBody(String id) {
        return universeBodiesByID.get(id);
    }

    public void createTemporaryGalaxy() {
        //Create a simple galaxy before generating this is handled
        UniverseBody galaxy = new UniverseBody("andromeda", null, 0, 0, 100000, 100000, 0, new Vector2(1, 1));
        universeBodiesByID.put("andromeda", galaxy);
        galaxies.add(galaxy);

        UniverseBody starSystem = new UniverseBody("starsystem101", null, 0, 0, 50000, 50000, 0, new Vector2(1, 1));
        universeBodiesByID.put("starsystem101", starSystem);
        galaxy.addChild(starSystem);

        UniverseBody solarSystem = new UniverseBody("ivusolaria", null, 0, 0, 10000, 10000, 0, new Vector2(1, 1));
        universeBodiesByID.put("ivusolaria", solarSystem);
        starSystem.addChild(solarSystem);

        Planet.Builder planetBuilder = new Planet.Builder("ives", "ivesiscool", solarSystem, 0, 0, 200, 200, new Vector2(-10, 0));
        planetBuilder.setRotation(45 * MathUtils.degreesToRadians);
        Planet earth = planetBuilder.build();
        universeBodiesByID.put("ives", earth);
        earth.setChunkBuilder(new SquarePlanet(earth, tileManager, uuidEntityManager, earth.gravity));
        solarSystem.addChild(earth);
    }
}
