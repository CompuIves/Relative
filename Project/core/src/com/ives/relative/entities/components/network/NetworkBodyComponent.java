package com.ives.relative.entities.components.network;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.BodyComponent;
import com.ives.relative.entities.factories.Factory;
import com.ives.relative.entities.systems.WorldSystem;

/**
 * Created by Ives on 8/12/2014.
 */
public class NetworkBodyComponent extends Component {
    public int z;
    public float x, y;
    public float vx, vy;
    public String worldID;

    public NetworkBodyComponent() {
    }

    public NetworkBodyComponent(BodyComponent bodyComponent) {
        Body b = bodyComponent.body;
        this.x = b.getPosition().x;
        this.y = b.getPosition().y;
        this.z = bodyComponent.z;
        this.vx = b.getLinearVelocity().x;
        this.vy = b.getLinearVelocity().y;
        this.worldID = bodyComponent.worldID;
    }

    public BodyComponent getComponent(Factory factory, Entity entity, Engine engine) {
        Body body = factory.createBody(entity, x, y, vx, vy, engine.getSystem(WorldSystem.class).getPlanet(worldID));
        return new BodyComponent(body, z, worldID);
    }
}
