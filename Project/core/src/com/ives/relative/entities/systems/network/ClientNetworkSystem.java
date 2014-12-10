package com.ives.relative.entities.systems.network;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ives.relative.core.Network;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.body.BodyComponent;

/**
 * Created by Ives on 8/12/2014.
 */
public class ClientNetworkSystem extends IntervalSystem {
    Network network;
    ImmutableArray<Entity> entities;

    public ClientNetworkSystem(float interval, Network network) {
        super(interval);
        this.network = network;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, NameComponent.class, InputComponent.class).get());
    }

    @Override
    protected void updateInterval() {

    }

    public void addEvent() {

    }
}
