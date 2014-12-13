package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 13/12/2014.
 *
 * HANDLED BY CLIENT
 */
public class PositionPacket extends UpdatePacket {
    Position position;
    Velocity velocity;
    long entityID;

    public PositionPacket() {
    }

    public PositionPacket(Entity entity, int sequence, long entityID) {
        super(sequence);
        this.position = entity.getWorld().getMapper(Position.class).get(entity);
        this.velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
        this.entityID = entityID;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity entity = game.world.getManager(NetworkManager.class).getNetworkEntity(entityID);
                //networkEntity.edit().add(position).add(velocity);
                if (entity != null) {
                    Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
                    if (physics != null) {
                        Position localPosition = entity.getWorld().getMapper(Position.class).get(entity);
                        Velocity localVelocity = entity.getWorld().getMapper(Velocity.class).get(entity);
                        localPosition.x = position.x;
                        localPosition.y = position.y;
                        localVelocity.vx = velocity.vx;
                        localVelocity.vy = velocity.vy;
                        Body body = physics.body;
                        body.getTransform().setPosition(new Vector2(position.x, position.y));
                        body.getLinearVelocity().set(velocity.vx, velocity.vy);
                    }
                } else {
                    //game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(entityID));
                }
            }
        });
    }
}