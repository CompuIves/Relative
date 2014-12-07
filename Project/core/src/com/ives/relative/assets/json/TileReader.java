package com.ives.relative.assets.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 6/12/2014.
 */
public class TileReader {

    public static SolidTile readFile(FileHandle file) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        SolidTile tile = json.fromJson(SolidTile.class, file);
        processID(file, tile);
        if(processTexture(file.parent().parent().path() + "/", tile))
            return tile;
        else return null;
    }

    public static boolean processTexture(String root, SolidTile tile) {
        if(tile.texture == null) {
            tile.texture = "tiles/" + tile.id + ".png";
        }
        try {
            tile.textureRegion = (new TextureRegion(new Texture(Gdx.files.local(root + tile.texture))));
            return true;
        } catch (Exception e) {
            Gdx.app.error("TextureLoading", "Couldn't load the texture for: " + tile.id + ", ignoring block.");
            return false;
        }
    }

    public static void processID(FileHandle fileHandle, SolidTile tile) {
        if(tile.id == null || tile.id.equals("")) {
            tile.id = fileHandle.nameWithoutExtension();
        }
    }
}
