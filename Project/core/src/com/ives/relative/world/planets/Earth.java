package com.ives.relative.world.planets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.tiles.Tile;
import com.ives.relative.entities.tiles.TileManager;
import com.ives.relative.entities.tiles.TilePos;
import com.ives.relative.world.Planet;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public class Earth extends Planet {
    World world;
    int gravity = -10;

    public Earth() {
        this.world = new World(new Vector2(0, gravity), true);
        worldTiles = new HashMap<TilePos, Tile>();
        tileManager = new TileManager(this);
        generatePlanet();
    }

    @Override
    public void generatePlanet() {
        for(int row = 0; row < 12; row++) {
            for (int column = 0; column < 200; column++) {
                worldTiles.put(new TilePos(this, column, row), tileManager.placeTile("dirt", column, row));
            }
        }
    }

    @Override
    public Tile getTile(int x, int y) {
        return worldTiles.get(new TilePos(this, x, y));
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public TileManager getTileManager() {
        return tileManager;
    }

    @Override
    public void timeStep(float timeStep, int velocityIterations, int positionIterations) {
        world.step(timeStep, velocityIterations, positionIterations);
    }
}
