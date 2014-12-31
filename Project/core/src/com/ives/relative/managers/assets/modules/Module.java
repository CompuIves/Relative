package com.ives.relative.managers.assets.modules;

/**
 * Created by Ives on 7/12/2014.
 * This is just a standard info-object of a module.
 */
public class Module {
    public String name;
    public String version;
    public String location;

    public Module() {
    }

    public Module(String name, String version, String location) {
        this.version = version;
        this.name = name;
        this.location = location;
    }
}
