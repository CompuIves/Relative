package com.ives.relative.core.packets.handshake.planet;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.core.network.networkentity.NetworkEntity;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.ConnectionSuccessful;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.planet.Gravity;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.PlanetManager;

/**
 * Created by Ives on 10/12/2014.
 * Handles the beautiful world
 * <p/>
 * HANDLED BY CLIENT
 */
public class PlanetPacket extends Packet {
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
                WorldC worldC = game.world.getMapper(WorldC.class).get(planet);
                Gravity gravity = game.world.getMapper(Gravity.class).get(planet);
                worldC.world = new World(new Vector2(gravity.x, gravity.y), true);

                PlanetManager planetManager = game.world.getManager(PlanetManager.class);
                planetManager.addPlanet(game.world.getMapper(Name.class).get(planet).internalName, planet);
                planetManager.generateTerrain(planet);

                game.world.getManager(NetworkManager.class).updateEntity(networkPlanet.id, planet);
                //game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestWorldSnapshot());
                game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new ConnectionSuccessful());
            }
        });
    }
}
