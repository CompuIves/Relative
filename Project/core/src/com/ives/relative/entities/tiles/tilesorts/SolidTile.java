package com.ives.relative.entities.tiles.tilesorts;

import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Ives on 2/12/2014.
 */
public class SolidTile {
    String id;
    Texture texture;
    int durability;
    float movementMultiplier;

    public int width = 1, height = 1;

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

    public SolidTile setMovementMultiplier(float movementMultiplier) {
        this.movementMultiplier = movementMultiplier;
        return this;
    }

    public SolidTile setWidth(int width) {
        this.width = width;
        return this;
    }

    public SolidTile setHeight(int height) {
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
}
