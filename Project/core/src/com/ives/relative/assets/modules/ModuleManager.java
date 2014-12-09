package com.ives.relative.assets.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ives.relative.assets.AssetsDB;
import com.ives.relative.assets.modules.json.TileReader;
import com.ives.relative.core.GameManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 6/12/2014.
 */
public class ModuleManager {
    GameManager game;

    ArrayList<Module> modules;

    boolean isServer;

    public ModuleManager(GameManager game) {
        this.game = game;
        this.isServer = game.isServer();
        modules = new ArrayList<Module>();
    }

    /**
     * Get the modules in the module folder
     */
    public void indexModules() {
        FileHandle[] moduleFolders = indexFiles(AssetsDB.MODULES);
        indexModules(moduleFolders);
    }

    public void indexModules(FileHandle[] moduleFolders) {
        if(moduleFolders != null) {
            for(FileHandle moduleFileHandler : moduleFolders) {
                //If the folder is a directory it is a module, if the folder is named Server just ignore (server is the cache folder for the server mods)
                if(moduleFileHandler.isDirectory()) {
                    //If the folder name is cache and the endpoint is a client then index the cacheModules too
                    if (moduleFileHandler.name().equals("cache") && !isServer) {
                        indexModules(indexFiles(moduleFileHandler.path()));
                    }
                    //Add the module to the module list
                    addModule(moduleFileHandler);
                }
            }
        }
    }

    /**
     * Compares local module list with the given module list.
     *
     * @param remoteModules The remote module list
     * @return The modules which the local object doesn't have or are outdated
     */
    public List<Module> compareModuleLists(List<Module> remoteModules) {
        List<Module> deltaModules = new ArrayList<Module>(remoteModules);
        for(Module remoteModule : remoteModules) {
            for (Module module : modules) {
                if (module.name.equals(remoteModule.name)) {
                    if (module.version.equals(remoteModule.version)) {
                        deltaModules.remove(remoteModule);
                    }
                }
            }
        }
        return deltaModules;
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
        FileHandle moduleJSON = new FileHandle(fileHandle.path() + "/" + fileHandle.name() + ".json");
        if (moduleJSON.exists()) {
            JsonValue jsonValue = reader.parse(moduleJSON);
            String version = jsonValue.get("version").asString();
            modules.add(new Module(name, version, fileHandle));
        }
    }

    public void loadModules() {
        for(Module module : modules) {
            //Get all the mod folders in it, for example tiles
            loadModule(module);
        }
    }

    public void loadModules(List<Module> loadModules) {
        for (Module module : loadModules) {
            if (modules.contains(module)) {
                loadModule(module);
            }
        }
    }

    private void loadModule(Module module) {
        FileHandle[] fileHandles = indexFiles(module.location.path());
        for(FileHandle fileHandle : fileHandles) {
            if(fileHandle.name().equalsIgnoreCase("tiles")) {
                readTiles(fileHandle.list(".json"));
            }
        }
    }

    private void readTiles(FileHandle[] fileHandles) {
        for(FileHandle fileHandle : fileHandles) {
            SolidTile tile = TileReader.readFile(fileHandle);
            if(tile != null) {
                game.tileManager.addTile(tile.id, tile);
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
