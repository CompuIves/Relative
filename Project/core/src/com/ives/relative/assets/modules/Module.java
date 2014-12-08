package com.ives.relative.assets.modules;

import com.badlogic.gdx.files.FileHandle;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.ArrayList;

/**
 * Created by Ives on 7/12/2014.
 */
public class Module {
    public String version;
    public String name;
    public transient FileHandle location;
    public transient ArrayList<SolidTile> tiles;

    public Module(String version, String name, FileHandle location) {
        this.version = version;
        this.name = name;
        this.location = location;
    }
}
