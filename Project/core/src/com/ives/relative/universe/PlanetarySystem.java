package com.ives.relative.universe;

import com.ives.relative.universe.chunks.builders.SquarePlanet;

/**
 * Created by Ives on 18/1/2015.
 */
public class PlanetarySystem extends UniverseBody {

    public PlanetarySystem(SolarSystem solarSystem, int x, int y, int width, int height) {
        super(x, y, width, height, solarSystem);

        Planet.Builder planetBuilder = new Planet.Builder("earth", "ivesiscool", this, 600, 400, 200, 200, new SquarePlanet());
        children.add(planetBuilder.build());
    }
}
