package com.ives.relative.managers.assets.modules.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.ives.relative.managers.SolidTile;

import java.io.File;

/**
 * Created by Ives on 6/12/2014.
 */
public class TileReader {

    public static SolidTile readFile(FileHandle file, String moduleRoot) {
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        SolidTile tile = json.fromJson(SolidTile.class, file);
        processID(file, tile);
        if (processTexture(moduleRoot + File.separator, file, tile))
            return tile;
        else return null;
    }

    public static boolean processTexture(String root, FileHandle file, SolidTile tile) {
        if (tile.texture == null) {
            tile.texture = file.parent().path() + File.separator + tile.id + ".png";
        } else {
            tile.texture = root + tile.texture;
        }
        try {
            tile.textureRegion = (new TextureRegion(new Texture(Gdx.files.local(tile.texture))));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.error("TextureLoading", "Couldn't load the texture for: " + tile.id + ", ignoring block.");
            return false;
        }
    }

    public static void processID(FileHandle fileHandle, SolidTile tile) {
        if (tile.id == null || tile.id.equals("")) {
            tile.id = fileHandle.nameWithoutExtension();
        }
    }
}
