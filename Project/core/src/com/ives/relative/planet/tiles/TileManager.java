package com.ives.relative.planet.tiles;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.factories.TileFactory;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public class TileManager {
    public HashMap<String, SolidTile> solidTiles;
    GameManager game;

    public TileManager(GameManager game) {
        solidTiles = new HashMap<String, SolidTile>();
        this.game = game;
    }

    public static PolygonShape getCube(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    public Entity createTile(String worldID, float x, float y, int z, String tileID, boolean gravity) {
        if(solidTiles.get(tileID) != null) {
            Entity world = game.engine.getSystem(WorldSystem.class).getPlanet(worldID);
            Entity e = TileFactory.createTile(worldID, Mappers.world.get(world).world, x, y, z, solidTiles.get(tileID), gravity);
            game.engine.addEntity(e);
            return e;
        } else {
            Gdx.app.error("WorldBuilding " + (game.isServer() ? "Server" : "Client"), "Couldn't load block with id: " + tileID +
                    " with position " + x + ", " + y + ", " + z + ", ignoring the block for now.");
            return null;
        }
    }

    public void addTile(String id, SolidTile tile) {
        solidTiles.put(id, tile);
    }
}
