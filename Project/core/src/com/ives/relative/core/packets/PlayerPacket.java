package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.factories.PlayerFactory;

/**
 * Created by Ives on 7/12/2014.
 */
public class PlayerPacket implements Packet {
    String internalName;
    String realName;
    String worldID;
    float x, y;
    int z;

    public PlayerPacket() {
    }

    public PlayerPacket(String internalName, String realName, String worldID, float x, float y, int z) {
        this.internalName = internalName;
        this.realName = realName;
        this.worldID = worldID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void handle(final GameManager game) {
        System.out.println("IT FRICKING WORKS");
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                World world = null;
                ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(WorldComponent.class, NameComponent.class).get());
                for(Entity e : entities) {
                    if(Mappers.name.get(e).internalName.equals(worldID)) {
                        world = Mappers.world.get(e).world;
                    }
                }
                game.engine.addEntity(PlayerFactory.createPlayer(internalName, realName, world, new Vector2(x, y), z));
            }
        });
    }
}
