package com.ives.relative.assets.json;

import com.badlogic.gdx.files.FileHandle;
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
        tile.processID(file);
        if(tile.processTexture(file.parent().parent().path() + "/"))
            return tile;
        else return null;
    }
}
