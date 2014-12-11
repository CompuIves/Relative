package com.ives.relative.core.packets.handshake.planet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.components.planet.GravityComponent;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 10/12/2014.
 * Handles the beautiful world
 * <p/>
 * HANDLED BY CLIENT
 */
public class PlanetPacket implements Packet {
    NetworkEntity planet;

    public PlanetPacket() {
    }

    public PlanetPacket(Entity planet) {
        this.planet = new NetworkEntity(planet);
    }

    @Override
    public void response(final GameManager game) {

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity e = planet.createEntity(game.planetFactory, game.engine);
                WorldComponent worldComponent = Mappers.world.get(e);
                GravityComponent gravityComponent = Mappers.gravity.get(e);
                worldComponent.world = new World(new Vector2(gravityComponent.gravityX, gravityComponent.gravityY), true);
                game.terrainGenerator.generateTerrain(e);
                game.engine.addEntity(e);
            }
        });

    }
}
