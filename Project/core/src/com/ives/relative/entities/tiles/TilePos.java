package com.ives.relative.entities.tiles;

import com.ives.relative.world.Planet;

/**
 * Created by Ives on 2/12/2014.
 */
public class TilePos {
    int x, y, z;
    Planet planet;

    public TilePos(Planet planet, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.planet = planet;
    }

    public TilePos(Planet planet, int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0;
        this.planet = planet;
    }
}
