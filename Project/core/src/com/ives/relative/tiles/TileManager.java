package com.ives.relative.tiles;

import com.badlogic.gdx.physics.box2d.PolygonShape;

/**
 * Created by Ives on 12/1/2014.
 */
public class TileManager {
    static int tileX = 20;
    static int tileY = 20;

    public static int getX(int xPos) {
        return xPos - (xPos % tileX);
    }

    /**
     * This returns the real Y position according to the tileset, there is +tileY because otherwise the tile above the
     * clicked one is used.
     * For example raw y = 84
     * real y = 84 - 4 - 20 = 60
     * @param yPos raw Y position
     * @return tile y position
     */
    public static int getY(int yPos) {
        return yPos + (yPos % tileY);
    }

    public static int getXSize(int x) {
        return tileX * x;
    }

    public static int getYSize(int y) {
        return tileY * y;
    }

    public static PolygonShape getCube(int width, int height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width * tileX / 2, height * tileY / 2);
        return polygonShape;
    }
}
