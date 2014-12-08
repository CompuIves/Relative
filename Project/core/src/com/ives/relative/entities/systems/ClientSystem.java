package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.ToServerPositionPacket;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 8/12/2014.
 */
public class ClientSystem extends IntervalSystem {
    Network network;
    ImmutableArray<Entity> entities;
    public ClientSystem(float interval, Network network) {
        super(interval);
        this.network = network;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, NameComponent.class, InputComponent.class).get());
    }

    @Override
    protected void updateInterval() {
        for(Entity entity : entities) {
            String name = Mappers.name.get(entity).internalName;
            Body body = Mappers.body.get(entity).body;
            network.sendObjectUDPtoServer(new ToServerPositionPacket(body.getPosition().x, body.getPosition().y, body.getLinearVelocity().x, body.getLinearVelocity().y, name));
        }
    }
}
