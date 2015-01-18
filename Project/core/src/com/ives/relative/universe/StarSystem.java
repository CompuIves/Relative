package com.ives.relative.universe;

/**
 * Created by Ives on 18/1/2015.
 */
public class StarSystem extends UniverseBody {

    public StarSystem(Galaxy galaxy, int x, int y, int width, int height) {
        super(galaxy, x, y, width, height);

        //Create standard solarsystem before generating code
        children.add(new SolarSystem(this, 300, 300, 4000, 4000));
    }
}
