package com.ives.relative.universe;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.builders.SquarePlanet;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 18/1/2015.
 */
@Wire
public class UniverseManager extends Manager {
    private final Array<UniverseBody> galaxies;
    private final Array<UniverseBody> starSystems;
    private final Array<UniverseBody> solarSystems;
    private final Array<UniverseBody> planetarySystems;
    private final Array<UniverseBody> planets;
    private final String seed;
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public UniverseManager(String seed) {
        this.seed = seed;
        galaxies = new Array<UniverseBody>();

        starSystems = new Array<UniverseBody>();
        solarSystems = new Array<UniverseBody>();
        planetarySystems = new Array<UniverseBody>();
        planets = new Array<UniverseBody>();
    }

    @Override
    protected void initialize() {
        createTemporaryGalaxy();
    }

    public UniverseBody getGalaxy(int x, int y) {
        for (UniverseBody galaxy : galaxies) {
            if (galaxy.isInBody(x, y)) {
                return galaxy;
            }
        }
        return null;
    }

    public void createTemporaryGalaxy() {
        //Create a simple galaxy before generating this is handled
        UniverseBody galaxy = new UniverseBody(null, 0, 0, 100000, 100000);
        galaxies.add(galaxy);

        UniverseBody starSystem = new UniverseBody(galaxy, 0, 0, 50000, 50000);
        galaxy.addChild(starSystem);

        UniverseBody solarSystem = new UniverseBody(starSystem, 0, 0, 10000, 10000);
        starSystem.addChild(solarSystem);

        UniverseBody planetarySystem = new UniverseBody(solarSystem, 0, 0, 5000, 5000);
        solarSystem.addChild(planetarySystem);

        Planet.Builder planetBuilder = new Planet.Builder("earth", "ivesiscool", planetarySystem, 0, 0, 200, 200, new Vector2(-10, 0));
        Planet earth = planetBuilder.build();
        earth.setChunkBuilder(new SquarePlanet(earth, tileManager, uuidEntityManager, earth.gravity));
        planetarySystem.addChild(earth);
    }
}
