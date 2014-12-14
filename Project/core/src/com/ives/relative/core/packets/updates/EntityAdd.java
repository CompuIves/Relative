package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.network.networkentity.NetworkEntity;
import com.ives.relative.entities.components.client.InputC;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class EntityAdd extends UpdatePacket {
    NetworkEntity networkEntity;

    public EntityAdd() {
    }

    public EntityAdd(Entity entity, int sequence, long id) {
        super(sequence);
        this.networkEntity = new NetworkEntity(entity, id);
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity e = networkEntity.createEntity(game.world);
                ClientManager clientManager = (ClientManager) game;
                if (networkEntity.id == clientManager.playerNetworkID) {
                    e.edit().add(new InputC());
                }
            }
        });
    }


}
