package com.ives.relative.network.packets.handshake;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
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
        Array<Entity> entities = new Array<Entity>();
        Array<Long> ids = new Array<Long>();
        for (Entity entity : networkEntities) {
            //Filter planets out
            if (entity.getComponent(WorldC.class) == null) {
                ids.add(game.world.getManager(NetworkManager.class).getNetworkID(entity));
                entities.add(entity);
                game.network.sendObjectTCP(connection, new CreateEntityPacket(entity, game.world.getManager(NetworkManager.class).getNetworkID(entity)));
            }
        }
        //game.network.sendObjectTCP(connection, new WorldSnapshotPacket(entities, ids));
    }
}
