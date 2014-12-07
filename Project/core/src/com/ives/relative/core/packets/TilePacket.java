package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.WorldComponent;

/**
 * Created by Ives on 7/12/2014.
 */
public class TilePacket implements Packet {
    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
                for(Entity entity : entities) {
                    //game.terrainGenerator.generateTerrain(entity);
                    uniqueMaker();
                }
            }
        });

    }

    public void uniqueMaker() {
        System.out.println("COOKIE");
    }
}
