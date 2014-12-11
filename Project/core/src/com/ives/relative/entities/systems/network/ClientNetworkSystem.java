package com.ives.relative.entities.systems.network;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.ives.relative.core.Network;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.InputComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 8/12/2014.
 */
public class ClientNetworkSystem extends IntervalSystem {
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

    public void addCommandPacket(Command command, float delta) {
        commandList.add(command);
    }
}
