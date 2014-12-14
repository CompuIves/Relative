package com.ives.relative.network.packets.updates;

import com.artemis.Entity;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.network.packets.UpdatePacket;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class PositionPacket extends UpdatePacket {
    public float x, y, rotation;
    public float vx, vy;
    public long entityID;

    public PositionPacket() {
    }

    public PositionPacket(Entity entity, int sequence, long entityID) {
        super(sequence, entityID);
        Position position = entity.getWorld().getMapper(Position.class).get(entity);
        Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
        this.x = position.x;
        this.y = position.y;
        this.rotation = position.rotation;

        this.vx = velocity.vx;
        this.vy = velocity.vy;

        this.entityID = entityID;
    }
}