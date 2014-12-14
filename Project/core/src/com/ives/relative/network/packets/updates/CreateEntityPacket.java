package com.ives.relative.network.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.network.networkentity.NetworkEntity;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class CreateEntityPacket extends ResponsePacket {
    NetworkEntity networkEntity;

    public CreateEntityPacket() {
    }

    public CreateEntityPacket(Entity entity, long id) {
        this.networkEntity = new NetworkEntity(entity, id);
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity e = networkEntity.createEntity(game.world);
                ClientNetworkSystem clientNetworkSystem = game.world.getSystem(ClientNetworkSystem.class);
                if (networkEntity.id == clientNetworkSystem.getPlayerID()) {
                    e.edit().add(new InputC());
                }
            }
        });
    }


}
