package com.ives.relative.universe;

/**
 * Created by Ives on 18/1/2015.
 */
public class SolarSystem extends UniverseBody {
    public SolarSystem(StarSystem starSystem, int x, int y, int width, int height) {
        super(starSystem, x, y, width, height);

        children.add(new PlanetarySystem(this, 300, 300, 3000, 3000));
    }
}
