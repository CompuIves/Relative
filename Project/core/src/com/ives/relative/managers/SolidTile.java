package com.ives.relative.managers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Ives on 2/12/2014.
 */
public class SolidTile {
    public String id;
    public String name;
    public String texture;
    public int durability = 10;
    public float movementMultiplier = 1f;
    public boolean gravity = false;
    public float width = 1, height = 1;
    public boolean isConnectable = false;
    public transient TextureRegion textureRegion;


    public SolidTile() {
    }
}
