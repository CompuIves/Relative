package com.ives.relative.planet.tiles.tilesorts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;

/**
 * Created by Ives on 2/12/2014.
 */
public class SolidTile implements Packet {
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

    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.tileManager.addTile(id, SolidTile.this);
            }
        });

    }
}
