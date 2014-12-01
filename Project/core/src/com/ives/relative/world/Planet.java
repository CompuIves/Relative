package com.ives.relative.world;

import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.tiles.Tile;

/**
 * Created by Ives on 12/1/2014.
 */
public interface Planet {
    int gravity = 0;

    World world = null;

    public void generatePlanet();

    public void placeTile(int x, int y);

    public Tile getTile(int x, int y);

    public World getWorld();

    public void timeStep(float timeStep, int velocityIterations, int positionIterations);
}
