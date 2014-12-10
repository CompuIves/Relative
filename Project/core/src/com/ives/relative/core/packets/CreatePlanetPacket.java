package com.ives.relative.core.packets;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.systems.WorldSystem;

/**
 * Created by Ives on 7/12/2014.
 *
 * CLIENT
 */
public class CreatePlanetPacket implements Packet {
    String world;
    public CreatePlanetPacket() {
    }

    public CreatePlanetPacket(String world) {
        this.world = world;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.terrainGenerator.generateTerrain(game.engine.getSystem(WorldSystem.class).getPlanet(world));
            }
        });
    }
}
