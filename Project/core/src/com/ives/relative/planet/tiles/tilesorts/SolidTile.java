package com.ives.relative.planet.tiles.tilesorts;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Ives on 2/12/2014.
 */
public class SolidTile {
    String id;
    String publicName = "";
    Texture texture;
    int durability;
    float movementMultiplier;

    boolean affectGravity = false;

    float width = 1, height = 1;

    boolean isConnectable;

    public SolidTile setId(String id) {
        this.id = id;
        return this;
    }

    public SolidTile setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }

    public SolidTile setDurability(int durability) {
        this.durability = durability;
        return this;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public SolidTile setMovementMultiplier(float movementMultiplier) {
        this.movementMultiplier = movementMultiplier;
        return this;
    }

    public SolidTile setWidth(float width) {
        this.width = width;
        return this;
    }

    public SolidTile setHeight(float height) {
        this.height = height;
        return this;
    }

    public SolidTile setConnectable(boolean isConnectable) {
        this.isConnectable = isConnectable;
        return this;
    }

    public int getDurability() {
        return durability;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isAffectGravity() {
        return affectGravity;
    }

    public SolidTile setAffectGravity(boolean affectGravity) {
        this.affectGravity = affectGravity;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getPublicName() {
        return publicName;
    }
}
