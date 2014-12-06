package com.ives.relative.assets.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by Ives on 6/12/2014.
 */
public class JSONIndexer {

    public void start() {
        FileHandle[] modules = indexFiles("modules");
        if(modules != null) {
            for(FileHandle fileHandle : modules) {
                if(fileHandle.isDirectory()) {
                    FileHandle[] categories = indexFiles("modules/" + fileHandle.name());
                    processCategories(categories);
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

    private void processCategories(FileHandle[] fileHandles) {
        for(FileHandle fileHandle : fileHandles) {
            if(fileHandle.name().equalsIgnoreCase("tiles")) {
                //TODO look at this
                TileReader.readFile(fileHandle.file());
            }
        }
    }
}
