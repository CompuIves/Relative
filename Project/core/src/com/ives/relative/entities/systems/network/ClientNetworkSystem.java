package com.ives.relative.entities.systems.network;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ives.relative.core.Network;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.observer.EntityChangeObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 8/12/2014.
 */
public class ClientNetworkSystem extends IntervalSystem implements EntityChangeObserver {
    Network network;
    ImmutableArray<Entity> entities;

    int sequence;
    List<Command> commandList;

    public ClientNetworkSystem(float interval, Network network) {
        super(interval);
        this.network = network;

        commandList = new ArrayList<Command>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(InputComponent.class).get());
    }

    @Override
    protected void updateInterval() {

    }

    @Override
    public void onEntityChange(Entity entity, Event event) {
        System.out.println("RECEIVED AN OBJECT!");
    }
}
