package com.ives.relative.universe;

import com.badlogic.gdx.utils.Array;


/**
 * Created by Ives van Hoorne on 5/21/2015.
 */
public class SolarSystem {
    final Array<Planet> planets;
    public final Star sun;

    public SolarSystem() {
        planets = new Array<Planet>();
        sun = new Star(10000, 100);
    }

    public void addPlanet(Planet planet) {
        planets.add(planet);
    }
}
