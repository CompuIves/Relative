package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.PositionComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.systems.WorldSystem;

/**
 * Created by Ives on 7/12/2014.
 */
public class CreatePlanetPacket implements Packet {
    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                //Get the player
                ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(InputComponent.class, PositionComponent.class).get());
                String worldID = Mappers.position.get(entities.first()).world;
                game.terrainGenerator.generateTerrain(game.engine.getSystem(WorldSystem.class).getPlanet(worldID));
            }
        });
    }
}
