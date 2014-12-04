package com.ives.relative.planet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 4/12/2014.
 */
public class TerrainGenerator {


    public void generateTerrain(Entity e) {
        World world = Mappers.world.get(e).world;
        for(int y = 1; y < 7; y++) {
            for(int x = 0; x < 100; x++)
                GameManager.tileManager.createTile(world, x, y, 0, "dirt", false);
        }

        for(int y = 0; y < 1; y++) {
            for(int x = 0; x < 200; x++) {
                GameManager.tileManager.createTile(world, x, y, 0, "bedrock", false);
            }
        }

        GameManager.tileManager.createTile(world, 20, 15, 0, "dirt", true);
        GameManager.tileManager.createTile(world, 25, 15, 0, "dirt", true);
        GameManager.tileManager.createTile(world, 23, 20, 0, "dirt", true);
        GameManager.tileManager.createTile(world, 30, 13, 0, "dirt", true);
        GameManager.tileManager.createTile(world, 30, 18, 0, "dirt", true);
    }
}
