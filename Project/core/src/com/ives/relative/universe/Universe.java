package com.ives.relative.universe;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 18/1/2015.
 */
public class Universe {
    private final Array<Galaxy> galaxies;
    private final String seed;

    public Universe(String seed) {
        galaxies = new Array<Galaxy>();
        this.seed = seed;

        //Create a simple galaxy before generating this is handled
        galaxies.add(new Galaxy(0, 0, 10000, 10000));
    }

    public Array<Galaxy> getGalaxies() {
        return galaxies;
    }

    public Galaxy getGalaxy(int x, int y) {
        for (Galaxy galaxy : galaxies) {
            if (galaxy.isInBody(x, y)) {
                return galaxy;
            }
        }
        return null;
    }
}
