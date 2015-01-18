package com.ives.relative.universe;

/**
 * Created by Ives on 18/1/2015.
 */
public class Galaxy extends UniverseBody {

    public Galaxy(int x, int y, int width, int height) {
        super(null, x, y, width, height);

        //Create standard starsystem before generating code
        children.add(new StarSystem(this, 200, 200, 5000, 5000));
    }
}
