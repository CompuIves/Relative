package com.ives.relative.systems;

import com.artemis.systems.VoidEntitySystem;

/**
 * Created by ivesv on 5/25/2015.
 */
public abstract class ThreadVoidEntitySystem extends VoidEntitySystem implements Runnable {

    public abstract void run();

    @Override
    protected void processSystem() {
        run();
    }
}
