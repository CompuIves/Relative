package com.ives.relative.network.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.network.networkentity.NetworkEntity;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.ConnectionSuccessful;

/**
 * Created by Ives on 10/12/2014.
 * Handles the beautiful world
 * <p/>
 * HANDLED BY CLIENT
 */
public class PlanetPacket extends ResponsePacket {
    NetworkEntity networkPlanet;

    public PlanetPacket() {
    }

    public PlanetPacket(Entity planet, long id) {
        this.networkPlanet = new NetworkEntity(planet, id);
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity planet = networkPlanet.createEntity(game.world);
                PlanetManager planetManager = game.world.getManager(PlanetManager.class);
                planetManager.generateTerrain(planet);
                //game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestWorldSnapshot());
                game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new ConnectionSuccessful());
            }
        });
    }
}
