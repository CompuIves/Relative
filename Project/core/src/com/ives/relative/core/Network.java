package com.ives.relative.core;

import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    public void sendEntityToAll(Entity e) {}
}
