package com.ives.relative.network.packets.handshake;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.planet.RequestPlanetPacket;
import com.ives.relative.systems.client.ClientNetworkSystem;

/**
 * Created by Ives on 13/12/2014.
 * Assigns the ID of the player which can be controlled to the player
 * <p/>
 * HANDLED BY CLIENT
 */
public class AssignPlayer extends ResponsePacket {
    int id;

    public AssignPlayer() {
    }

    public AssignPlayer(int id) {
        this.id = id;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.world.getSystem(ClientNetworkSystem.class).registerPlayer(id);
                game.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestPlanetPacket());
            }
        });
    }
}
