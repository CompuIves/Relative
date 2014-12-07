package com.ives.relative.planet.tiles.tilesorts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;

/**
 * Created by Ives on 2/12/2014.
 */
public class SolidTile {
    String id;
    String name;
    String texture;
    TextureRegion textureRegion;
    int durability = 10;
    float movementMultiplier = 1f;
    boolean gravity = false;
    float width = 1, height = 1;
    boolean isConnectable = false;


    public SolidTile() {
    }

    public String getTexture() {
        return texture;
    }

    public SolidTile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SolidTile setId(String id) {
        this.id = id;
        return this;
    }

    public SolidTile setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        return this;
    }

    public SolidTile setTextureRegion(Texture texture) {
        this.textureRegion = new TextureRegion(texture);
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

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public boolean isGravity() {
        return gravity;
    }

    public SolidTile setGravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean processTexture(String root) {
        if(texture == null) {
            texture = "tiles/" + id + ".png";
        }
        try {
            setTextureRegion(new Texture(Gdx.files.local(root + texture)));
            return true;
        } catch (Exception e) {
            Gdx.app.error("TextureLoading", "Couldn't load the texture for: " + getId() + ", ignoring block.");
            return false;
        }
    }

    public SolidTile setTexture(String texture) {
        this.texture = texture;
        return this;
    }

    public void processID(FileHandle fileHandle) {
        if(id == null || id.equals("")) {
            id = fileHandle.nameWithoutExtension();
        }
    }
}
