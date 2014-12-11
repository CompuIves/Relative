package com.ives.relative.core;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Proxy {
    /**
     * Don't call directly from Proxy!
     */
    public static GameManager game;
    public Network network;

    public void update(float delta) {}

    public void registerSystems() {}
}
