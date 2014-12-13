package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.networkentity.NetworkEntity;
import com.ives.relative.entities.components.client.InputC;

/**
 * Created by Ives on 10/12/2014.
 * This packet provides every location of every walking entity on the world, this will be sent when the
 * player joins and when there are discrepancies between the information on the client and the server.
 *
 * HANDLED BY CLIENT
 */
public class WorldSnapshotPacket extends Packet {
    NetworkEntity[] networkEntities;
    int playerID;

    public WorldSnapshotPacket() {
    }

    public WorldSnapshotPacket(Array<Entity> entities, int playerID) {
        this.networkEntities = new NetworkEntity[entities.size];
        this.playerID = playerID;
        for (int i = 0; i < entities.size; i++) {
            this.networkEntities[i] = new NetworkEntity(entities.get(i), NetworkEntity.Type.PLAYER);
        }

    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                for (NetworkEntity networkEntity : networkEntities) {
                    if (game.world.getEntity(networkEntity.entityID) != null) {
                        networkEntity.createEntity(game.world);
                    } else {
                        networkEntity.createEntity(game.world);
                    }
                }
                Entity player = game.world.getEntity(playerID);
                player.edit().add(new InputC());
                game.world.getManager(TagManager.class).register("player", player);
            }
        });
    }
}
