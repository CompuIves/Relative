package com.ives.relative.planet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 4/12/2014.
 */
public class TerrainGenerator {
    GameManager game;

    public TerrainGenerator(GameManager game) {
        this.game = game;
    }

    public void generateTerrain(Entity world) {
        String worldID = Mappers.name.get(world).internalName;
        for(int y = 1; y < 7; y++) {
            for(int x = 0; x < 200; x++)
                game.tileManager.createTile(worldID, x, y, 0, "dirt", false);
        }
        for(int y = 0; y < 1; y++) {
            for(int x = 0; x < 200; x++) {
                game.tileManager.createTile(worldID, x, y, 0, "bedrock", false);
            }
        }

        game.tileManager.createTile(worldID, 20, 15, 0, "dirt", true);
        game.tileManager.createTile(worldID, 25, 15, 0, "dirt", true);
        game.tileManager.createTile(worldID, 23, 20, 0, "dirt", true);
        game.tileManager.createTile(worldID, 30, 13, 0, "dirt", true);
        game.tileManager.createTile(worldID, 30, 18, 0, "dirt", true);
    }
}
