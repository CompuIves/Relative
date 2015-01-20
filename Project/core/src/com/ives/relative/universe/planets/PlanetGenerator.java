package com.ives.relative.universe.planets;

import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 4/1/2015.
 *
 * The official generator of a planet!
 */
@Wire
public class PlanetGenerator extends Manager {
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;

    public void generateTerrain(Chunk chunk) {

    }
}
