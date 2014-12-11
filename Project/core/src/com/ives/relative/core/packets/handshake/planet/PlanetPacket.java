package com.ives.relative.core.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.entities.components.planet.GravityComponent;
import com.ives.relative.entities.components.planet.WorldComponent;
import com.ives.relative.entities.managers.PlanetManager;

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
                Entity e = planet.createEntity(game.entityWorld);
                WorldComponent worldComponent = game.entityWorld.getMapper(WorldComponent.class).get(e);
                GravityComponent gravityComponent = game.entityWorld.getMapper(GravityComponent.class).get(e);
                worldComponent.world = new World(new Vector2(gravityComponent.gravityX, gravityComponent.gravityY), true);

                PlanetManager planetManager = game.entityWorld.getManager(PlanetManager.class);
                planetManager.generateTerrain(e);
            }
        });

    }
}
