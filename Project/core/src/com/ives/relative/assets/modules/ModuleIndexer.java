package com.ives.relative.assets.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ives.relative.assets.AssetsDB;
import com.ives.relative.assets.modules.Module;
import com.ives.relative.assets.modules.json.TileReader;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 6/12/2014.
 */
public class ModuleIndexer {
    TileManager tileManager;
    ArrayList<Module> modules;

    public ModuleIndexer(TileManager tileManager) {
        this.tileManager = tileManager;
        modules = new ArrayList<Module>();
    }

    /**
     * Get the modules in the module folder
     */
    public void indexModules(boolean cacheModules) {
        FileHandle[] moduleFolders = indexFiles(AssetsDB.MODULES);
        if(moduleFolders != null) {
            for(FileHandle moduleFileHandler : moduleFolders) {
                //If the folder is a directory it is a module, if the folder is named Server just ignore (server is the cache folder for the server mods)
                if(moduleFileHandler.isDirectory()) {
                    //If the cache modules shouldn't be checked
                    if(!cacheModules) {
                        if(!moduleFileHandler.name().equals("Server")) {
                            //Add the module to the module list of the server
                            addModule(moduleFileHandler);
                        }
                    } else {
                        addModule(moduleFileHandler);
                    }
                }
            }
        }
    }

    public List<Module> compareModuleLists(List<Module> remoteModules) {
        for(Module remoteModule : remoteModules) {

        }
        return null;
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

    public void addModule(FileHandle fileHandle) {
        String name = fileHandle.name();
        JsonReader reader = new JsonReader();
        JsonValue jsonValue = reader.parse(new FileHandle(fileHandle.path() + "/" + fileHandle.name() + ".json"));
        String version = jsonValue.get("version").asString();
        modules.add(new Module(name, version, fileHandle));
    }

    public void loadModules() {
        for(Module module : modules) {
            //Get all the mod folders in it, for example tiles
            moduleCategories(indexFiles(module.location.path()));
        }
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
