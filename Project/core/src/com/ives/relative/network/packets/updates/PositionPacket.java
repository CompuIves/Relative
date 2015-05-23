package com.ives.relative.network.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class PositionPacket extends UpdatePacket {
    public String universeBody;
    public float x, y, rotation;
    public float vx, vy;

    public float vr;


    public PositionPacket() {
    }

    public PositionPacket(Entity entity, int sequence, int entityID) {
        super(sequence, entityID);
        Location location = entity.getWorld().getMapper(Location.class).get(entity);
        Body body = entity.getWorld().getMapper(Physics.class).get(entity).body;
        Vector2 position = body.getPosition();
        float rotation = body.getAngle();
        Vector2 velocity = body.getLinearVelocity();
        float vr = body.getAngularVelocity();
        this.universeBody = location.space.id;
        this.x = position.x;
        this.y = position.y;
        this.rotation = rotation;

        this.vx = velocity.x;
        this.vy = velocity.y;
        this.vr = vr;

        this.entityID = entityID;
    }

    public PositionPacket(int sequence, int entityID, float x, float y, float rotation, float vx, float vy, float vr) {
        super(sequence, entityID);
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.vx = vx;
        this.vy = vy;
        this.vr = vr;
    }
}