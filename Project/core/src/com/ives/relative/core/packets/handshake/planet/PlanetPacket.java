package com.ives.relative.core.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.entities.components.planet.Gravity;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.PlanetManager;

/**
 * Created by Ives on 10/12/2014.
 * Handles the beautiful world
 * <p/>
 * HANDLED BY CLIENT
 */
public class PlanetPacket implements Packet {
    NetworkEntity networkPlanet;

    public PlanetPacket() {
    }

    public PlanetPacket(Entity planet) {
        this.networkPlanet = new NetworkEntity(planet);
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity planet = networkPlanet.createEntity(game.world);
                WorldC worldC = game.world.getMapper(WorldC.class).get(planet);
                Gravity gravity = game.world.getMapper(Gravity.class).get(planet);
                worldC.world = new World(new Vector2(gravity.x, gravity.y), true);

                PlanetManager planetManager = game.world.getManager(PlanetManager.class);
                planetManager.generateTerrain(planet);
            }
        });
    }
}
