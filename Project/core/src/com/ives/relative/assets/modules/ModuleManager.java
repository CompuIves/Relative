package com.ives.relative.assets.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ives.relative.assets.AssetsDB;
import com.ives.relative.assets.modules.json.TileReader;
import com.ives.relative.core.GameManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
     * Indexes the given location recursively, so every subdirectory will be indexed.
     *
     * @param location    The location which will be indexed
     * @param fileHandles The List where every filehandle will be added to. THIS WON'T RETURN A LIST, THIS WILL EDIT THE LIST GIVEN.
     */
    public static void indexFilesRecursively(String location, List<FileHandle> fileHandles) {
        try {
            for (FileHandle fileHandle : Arrays.asList(Gdx.files.local(location).list())) {
                fileHandles.add(fileHandle);
                if (fileHandle.isDirectory()) {
                    indexFilesRecursively(fileHandle.path(), fileHandles);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Get the modules in the module folder
     */
    public void indexModules() {
        FileHandle[] moduleFiles = indexFiles(AssetsDB.MODULES);
        indexModuleFolders(moduleFiles);
        indexModuleZips(moduleFiles);
    }

    private void indexModuleZips(FileHandle[] moduleFiles) {
        if (moduleFiles != null) {
            for (FileHandle moduleZip : moduleFiles) {
                if (!moduleZip.isDirectory()) {
                    if (moduleZip.extension().equals("zip")) {
                        String[] moduleInfo = moduleZip.nameWithoutExtension().split("-");
                        //If the zip is following name conventions
                        if (moduleInfo.length > 1) {
                            Module module = new Module(moduleInfo[0], moduleInfo[1], moduleZip.pathWithoutExtension());
                            if (!isModuleAdded(module)) {
                                //If unzipping doesn't go wrong
                                if (unZipModule(moduleInfo[0], moduleInfo[1], moduleZip.pathWithoutExtension().split("-")[0])) {
                                    //Add a new module with the name, but split off the version.
                                    addModule(new FileHandle(moduleZip.pathWithoutExtension().split("-")[0]));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void indexModuleFolders(FileHandle[] moduleFolders) {
        if(moduleFolders != null) {
            for(FileHandle moduleFileHandler : moduleFolders) {
                //If the folder is a directory it is a module, if the folder is named Server just ignore (server is the cache folder for the server mods)
                if(moduleFileHandler.isDirectory()) {
                    //If the folder name is cache and the endpoint is a client then index the cacheModules too
                    if (moduleFileHandler.name().equals("cache") && !isServer) {
                        indexModuleFolders(indexFiles(moduleFileHandler.path()));
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
        List<Module> deltaModules = new ArrayList<Module>(modules);
        return deltaModules;
        //This is a debugging thing
        /*
        for (Module module : modules) {
            for (Module remoteModule : remoteModules) {
                if (module.name.equals(remoteModule.name)) {
                    if (module.version.equals(remoteModule.version)) {
                        deltaModules.remove(module);
                    }
                }
            }
            return deltaModules;
            */
    }

    public boolean isModuleAdded(Module module) {
        //Check if the module is already in the folder
        for (Module localModule : modules) {
            if (localModule.name.equals(module.name)) {
                if (localModule.version.equals(module.version)) {
                    //Using a label to continue the nested loop
                    return true;
                } else {
                    System.out.println("CONFLICTING VERSIONS, BEWARE");
                }
            }
        }
        return false;
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
            Module module = new Module(name, version, fileHandle.path());
            if (!isModuleAdded(module))
                modules.add(module);
        }
    }

    public void loadModules() {
        StringBuilder builder = new StringBuilder();
        builder.append("Loaded ").append(modules.size()).append(" modules: ");
        for(Module module : modules) {
            //Get all the mod folders in it, for example tiles
            loadModule(module);
            builder.append('\n').append("- ").append(module.name).append("-").append(module.version);
        }
        System.out.println(builder.toString());
    }

    public void loadModules(List<Module> loadModules) {
        for (Module module : loadModules) {
            if (modules.contains(module)) {
                loadModule(module);
            }
        }
    }

    private void loadModule(Module module) {
        FileHandle[] fileHandles = indexFiles(module.location);
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

    //Gets only called by server, for converting zip to bytes
    public byte[] moduleToBytes(Module module) throws IOException {
        Path zip = Paths.get(Gdx.files.local(AssetsDB.MODULES).path() + File.separator + module.name + "-" + module.version + ".zip");

        //This line will prevent NPE's (NullPointerExceptions).
        byte[] bytes = new byte[0];
        if (zip != null) {
            bytes = Files.readAllBytes(zip);
            return bytes;
        }
        return bytes;
    }

    public void bytesToModule(byte[] bytes, String name, String version) throws IOException {
        Files.write(Paths.get(AssetsDB.MODULES + "cache" + File.separator + name + "-" + version + ".zip"), bytes, new OpenOption[StandardOpenOption.CREATE.ordinal()]);

    }

    /**
     * Zips all active modules to different .zips.
     */
    public void zipAllModules() {
        for (Module module : modules) {
            createZip(module);
        }
    }

    /**
     * Creates a zip from the module
     *
     * @param module the module which will be zipped
     */
    public void createZip(Module module) {
        String zipLocation = AssetsDB.MODULES + module.name + "-" + module.version + ".zip";
        if (Gdx.files.local(zipLocation).exists()) {
            return;
        }
        System.out.println("Zipping module: " + module.name + ", version: " + module.version);
        try {
            //A buffer for the writer to write to
            byte[] buffer = new byte[1024];

            FileOutputStream fos = new FileOutputStream(new File(zipLocation));
            ZipOutputStream zos = new ZipOutputStream(fos);

            List<FileHandle> fileHandles = new ArrayList<FileHandle>();
            //Get all the items which need to be read
            indexFilesRecursively(module.location, fileHandles);

            for (FileHandle fileHandle : fileHandles) {
                if (fileHandle.isDirectory())
                    continue;

                FileInputStream fis = new FileInputStream(fileHandle.file());
                //The split command is used because the path is for example "modules/relative", this will add the files to the root of the zip.
                zos.putNextEntry(new ZipEntry(fileHandle.path().split(AssetsDB.MODULES + module.name + "/")[1]));
                //Writes from the buffer to the ZipOutputStream
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean unZipModule(String name, String version, String directory) {
        FileHandle fileHandle = Gdx.files.local(AssetsDB.MODULES + name + "-" + version + ".zip");
        if (!fileHandle.exists())
            return false;

        System.out.println("Unzipping module: " + name + ", version: " + version);
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileHandle.file()));
            File moduleFile = Gdx.files.local(directory).file();
            //Create folders
            new File(moduleFile.getParent()).mkdirs();

            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String filePath = moduleFile.getPath() + File.separator + ze.getName();

                if (!ze.isDirectory()) {
                    extractFile(zis, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.error("FileError", "Couldn't unzip the module zip.");
        }
        return false;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        new File(filePath).getParentFile().mkdirs();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[1024];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public ArrayList<Module> getModules() {
        return modules;
    }
}
