package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.PhysicsPosition;

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

    public NetworkBodyComponent(PhysicsPosition physicsPosition) {
        Body b = physicsPosition.body;
        this.x = b.getPosition().x;
        this.y = b.getPosition().y;
        this.z = physicsPosition.z;
        this.vx = b.getLinearVelocity().x;
        this.vy = b.getLinearVelocity().y;
        this.worldID = physicsPosition.worldID;
    }

    public PhysicsPosition getComponent(Entity e) {
        //TODO GETCOMPONENT
        return null;
    }
}
