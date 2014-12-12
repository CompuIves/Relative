package com.ives.relative.core;

import com.artemis.World;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Proxy {
    /**
     * Don't call directly from Proxy!
     */
    public static GameManager game;
    public static World world;
    public Network network;

    public void update(float delta) {}

    public void registerSystems() {}
}
