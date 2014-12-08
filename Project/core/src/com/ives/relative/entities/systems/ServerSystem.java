package com.ives.relative.entities.systems;

import com.badlogic.ashley.systems.IntervalSystem;
import com.ives.relative.core.Network;

/**
 * Created by Ives on 8/12/2014.
 */
public class ServerSystem extends IntervalSystem {
    Network network;
    public ServerSystem(float interval, Network network) {
        super(interval);
        this.network = network;
    }

    @Override
    protected void updateInterval() {

    }
}
