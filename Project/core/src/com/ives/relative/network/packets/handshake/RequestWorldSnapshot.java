package com.ives.relative.network.packets.handshake;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.network.ServerNetworkSystem;

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
        ImmutableBag<Entity> networkEntities = game.world.getSystem(ServerNetworkSystem.class).getActives();
        NetworkManager networkManager = game.world.getManager(NetworkManager.class);
        for (Entity entity : networkEntities) {
            //Filter planets out
            if (entity.getComponent(WorldC.class) == null) {
                NetworkC networkC = game.world.getMapper(NetworkC.class).get(entity);
                game.network.sendObjectTCP(connection, networkManager.generateFullComponentPacket(entity, networkC.type));
            }
        }
        //game.network.sendObjectTCP(connection, new WorldSnapshotPacket(entities, ids));
    }
}
