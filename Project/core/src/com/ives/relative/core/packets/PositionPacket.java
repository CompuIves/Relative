package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 8/12/2014.
 *
 * CLIENT
 */
public class PositionPacket implements Packet {
    public float x, y;
    public float vx, vy;
    public String internalName;

    public PositionPacket() {
    }

    public PositionPacket(float x, float y, float vx, float vy, String internalName) {

        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.internalName = internalName;
    }

    @Override

    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(BodyComponent.class, NameComponent.class).get());
                for(Entity entity : entities) {
                    if(Mappers.name.get(entity).internalName.equals(internalName)) {
                        Body body = Mappers.body.get(entity).body;
                        body.setTransform(x, y, 0);
                        body.setLinearVelocity(vx, vy);
                    }
                }
            }
        });
    }
}
