package com.ives.relative.assets.modules;

import com.badlogic.gdx.files.FileHandle;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.ArrayList;

/**
 * Created by Ives on 7/12/2014.
 */
public class Module {
    public String name;
    public String version;
    public transient FileHandle location;
    public transient ArrayList<SolidTile> tiles;

    public Module() {
    }

    public Module(String name, String version, FileHandle location) {
        this.version = version;
        this.name = name;
        this.location = location;
    }
}
