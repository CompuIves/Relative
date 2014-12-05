package com.ives.relative.planet.tiles;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.*;
import com.ives.relative.entities.factories.TileFactory;
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

        //This is TEMPORARY
        solidTiles.put("dirt", new SolidTile().setDurability(1).setId("dirt").setTexture(new Texture("dirt.png")).setAffectGravity(true));
        solidTiles.put("bedrock", new SolidTile().setDurability(123).setId("bedrock").setTexture(new Texture("bedrock.png")));
    }

    public static PolygonShape getCube(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    public Entity createTile(World world, float x, float y, int z, String tileID, boolean gravity) {
        Entity e = TileFactory.createTile(world, x, y, z, solidTiles.get(tileID), gravity);
        game.engine.addEntity(e);
        return e;
    }

}
