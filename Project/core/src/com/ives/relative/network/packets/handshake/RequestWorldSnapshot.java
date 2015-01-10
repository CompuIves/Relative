package com.ives.relative.network.packets.handshake;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.systems.server.ServerNetworkSystem;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestWorldSnapshot extends ResponsePacket {

    public RequestWorldSnapshot() {
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ImmutableBag<Entity> networkEntities = game.world.getSystem(ServerNetworkSystem.class).getActives();
                NetworkManager networkManager = game.world.getManager(NetworkManager.class);
                for (Entity entity : networkEntities) {
                    //Filter planets out
                    if (!entity.getWorld().getMapper(WorldC.class).has(entity)) {
                        NetworkC networkC = game.world.getMapper(NetworkC.class).get(entity);
                        UpdatePacket packet = new UpdatePacket(0, networkManager.getNetworkID(entity));
                        packet.connection = connection;
                        game.world.getSystem(NetworkSendSystem.class).sendEntity(connection, entity);
                    }
                }
                //game.network.sendObjectTCP(connection, new WorldSnapshotPacket(entities, ids));
            }
        });
    }
}
