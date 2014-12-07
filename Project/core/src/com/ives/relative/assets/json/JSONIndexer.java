package com.ives.relative.assets.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ives.relative.assets.AssetsDB;
import com.ives.relative.core.GameManager;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 6/12/2014.
 */
public class JSONIndexer {
    TileManager tileManager;

    public JSONIndexer(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public void getModules() {
        FileHandle[] modules = indexFiles(AssetsDB.MODULES);
        if(modules != null) {
            for(FileHandle fileHandle : modules) {
                if(fileHandle.isDirectory()) {
                    FileHandle[] categories = indexFiles(AssetsDB.MODULES + fileHandle.name());
                    moduleCategories(categories);
                }
            }
        }
    }

    public FileHandle[] indexFiles(String location) {
        if(Gdx.files.local(location).exists()) {
            return Gdx.files.local(location).list();
        } else {
            if(!Gdx.files.local(location).file().mkdir()) {
                Gdx.app.error("CreateDir", "Couldn't create new directory " + location);
            }
        }
        return null;
    }

    private void moduleCategories(FileHandle[] fileHandles) {
        for(FileHandle fileHandle : fileHandles) {
            if(fileHandle.name().equalsIgnoreCase("tiles")) {
                readTiles(fileHandle.list(".json"));
            }
        }
    }

    private void readTiles(FileHandle[] fileHandles) {
        for(FileHandle fileHandle : fileHandles) {
            SolidTile tile = TileReader.readFile(fileHandle);
            if(tile != null)
                this.tileManager.addTile(tile.id, tile);
        }
    }
}
