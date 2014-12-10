package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.entities.components.InputComponent;

/**
 * Created by Ives on 7/12/2014.
 * Server sends a packet to the client with a player, this gets called when the client connects and when a new client connects
 * connection for knowing which client sent it (to take control over the player).
 *
 * CLIENT
 */
public class PlayerPacket implements Packet {
    int connection;
    NetworkEntity player;

    public PlayerPacket() {
    }

    public PlayerPacket(int connection, Entity player) {
        this.connection = connection;
        this.player = new NetworkEntity(player, "player.png");
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity entity = player.createEntity(GameManager.playerFactory, game.engine);
                //Add a inputcomponent for handling inputs to the player!
                if(game.proxy.network.connectionID == connection)
                    entity.add(new InputComponent());
                game.engine.addEntity(entity);
            }
        });
    }
}
