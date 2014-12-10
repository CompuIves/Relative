package com.ives.relative.entities.systems.network;

import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.Network;

/**
 * Created by Ives on 8/12/2014.
 */
public class ServerNetworkSystem extends IntervalSystem {
    Network network;

    public ServerNetworkSystem(float interval, Network network) {
        super(interval);
        this.network = network;
    }

    @Override
    protected void updateInterval() {
        //network.sendObjectToAllUDP();
    }

    public void addEvent(Vector2 position, Vector2 velocity) {

    }
}
