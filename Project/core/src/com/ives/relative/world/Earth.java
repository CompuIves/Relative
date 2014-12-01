package com.ives.relative.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.tiles.Tile;
import com.ives.relative.tiles.TileManager;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public class Earth implements Planet {
    World world;
    int gravity = -10;

    HashMap<Vector2, Tile> tiles;

    public Earth() {
        this.world = new World(new Vector2(0, gravity), true);
        tiles = new HashMap<Vector2, Tile>();
        generatePlanet();
    }

    @Override
    public void generatePlanet() {
        for(int i = 0; i < 70; i++) {
            tiles.put(new Vector2(TileManager.getX(TileManager.getXSize(i)), TileManager.getY(700)), new Tile(world, TileManager.getXSize(i), 700));
        }
    }

    @Override
    public void placeTile(int x, int y) {
        x = TileManager.getX(x);
        y = TileManager.getY(y);
        tiles.put(new Vector2(x, y), new Tile(world, x, y));
    }

    @Override
    public Tile getTile(int x, int y) {
        return tiles.get(new Vector2(x, y));
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void timeStep(float timeStep, int velocityIterations, int positionIterations) {
        world.step(timeStep, velocityIterations, positionIterations);
    }
}
