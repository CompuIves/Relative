package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;

/**
 * Created by Ives on 13/12/2014.
 *
 * HANDLED BY CLIENT
 */
public class PositionPacket extends UpdatePacket {
    Position position;
    Velocity velocity;
    int entityID;

    public PositionPacket() {
    }

    public PositionPacket(Entity entity, int sequence) {
        super(sequence);
        this.position = entity.getWorld().getMapper(Position.class).get(entity);
        this.velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
        this.entityID = entity.getId();
    }

    @Override
    public void response(GameManager game) {
        if (game.world.getEntityManager().getTotalAdded() > entityID) {
            Entity entity = game.world.getEntity(entityID);
            entity.edit().add(position).add(velocity);
            Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
            Body body = physics.body;
            body.getTransform().setPosition(new Vector2(position.x, position.y));
            body.getLinearVelocity().set(velocity.vx, velocity.vy);
        }

    }
}
