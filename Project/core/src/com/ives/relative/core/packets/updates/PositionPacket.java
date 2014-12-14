package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.core.packets.requests.RequestEntity;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class PositionPacket extends UpdatePacket {
    float x, y, rotation;
    float vx, vy;
    long entityID;

    public PositionPacket() {
    }

    public PositionPacket(Entity entity, int sequence, long entityID) {
        super(sequence);
        Position position = entity.getWorld().getMapper(Position.class).get(entity);
        Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
        Body body = entity.getWorld().getMapper(Physics.class).get(entity).body;
        this.x = body.getTransform().getPosition().x;
        this.y = body.getTransform().getPosition().y;
        this.rotation = body.getTransform().getRotation();

        this.vx = body.getLinearVelocity().x;
        this.vy = body.getLinearVelocity().y;

        this.entityID = entityID;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity entity = game.world.getManager(NetworkManager.class).getNetworkEntity(entityID);
                //networkEntity.edit().add(position).add(velocity);
                if (entity != null) {
                    Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
                    Body body = physics.body;
                    body.setTransform(x, y, rotation);
                    //body.setLinearVelocity(vx, vy);
                    Position localPosition = entity.getWorld().getMapper(Position.class).get(entity);
                    Velocity localVelocity = entity.getWorld().getMapper(Velocity.class).get(entity);
                    localPosition.x = x;
                    localPosition.y = y;
                    //localVelocity.vx = vx;
                    //localVelocity.vy = vy;
                } else {
                    game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(entityID));
                }
            }
        });
    }
}