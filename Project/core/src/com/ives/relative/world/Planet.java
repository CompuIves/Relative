package com.ives.relative.world;

import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.tiles.Tile;
import com.ives.relative.entities.tiles.TileManager;
import com.ives.relative.entities.tiles.TilePos;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public abstract class Planet {
    int gravity = 0;
    public TileManager tileManager = null;
    public HashMap<TilePos, Tile> worldTiles = null;

    World world = null;

    public abstract void generatePlanet();

    public abstract Tile getTile(int x, int y);

    public abstract World getWorld();

    public abstract TileManager getTileManager();

    public abstract void timeStep(float timeStep, int velocityIterations, int positionIterations);
}
