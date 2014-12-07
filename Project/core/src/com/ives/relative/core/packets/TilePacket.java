package com.ives.relative.core.packets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ives.relative.core.GameManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.nio.ByteBuffer;

/**
 * Created by Ives on 7/12/2014.
 */
public class TilePacket implements Packet {
    SolidTile tile;
    byte[] textureBytes;
    int width, height;
    int format;

    public TilePacket(SolidTile tile) {
        this.tile = tile;
        tile.textureRegion.getTexture().getTextureData().prepare();
        Pixmap pixmap = tile.textureRegion.getTexture().getTextureData().consumePixmap();
        ByteBuffer pixels = pixmap.getPixels();
        textureBytes = new byte[pixels.limit()];
        pixels.get(textureBytes, 0, textureBytes.length);

        format = pixmap.getFormat().ordinal();
        width = pixmap.getWidth();
        height = pixmap.getHeight();
    }

    public TilePacket() {}

    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.values()[format]);
                ByteBuffer pixels = pixmap.getPixels();
                pixels.clear();
                pixels.put(textureBytes);
                pixels.position(0);
                Texture texture = new Texture(pixmap);
                TextureRegion textureRegion = new TextureRegion(texture);
                tile.textureRegion = textureRegion;
                game.tileManager.addTile(tile.id, tile);
            }
        });
    }
}
