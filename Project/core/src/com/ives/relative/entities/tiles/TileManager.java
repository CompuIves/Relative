package com.ives.relative.entities.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ives.relative.entities.tiles.tilesorts.SolidTile;
import com.ives.relative.world.Planet;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public class TileManager {
    private Planet planet;
    public HashMap<String, SolidTile> solidTiles;

    public TileManager(Planet planet) {
        this.planet = planet;
        solidTiles = new HashMap<String, SolidTile>();

        //This is TEMPORARY
        solidTiles.put("dirt", new SolidTile().setDurability(1).setId("dirt").setTexture(new Texture("dirt.png")));
    }

    public static PolygonShape getCube(int width, int height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    public Tile placeTile(String tileID, int x, int y) {
        return new Tile(solidTiles.get(tileID), new TilePos(planet, x, y));
    }
}
