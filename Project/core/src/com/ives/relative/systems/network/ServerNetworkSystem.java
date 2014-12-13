package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.ives.relative.core.packets.updates.PositionPacket;
import com.ives.relative.core.server.ServerManager;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 13/12/2014.
 */
public class ServerNetworkSystem extends IntervalEntitySystem {
    public final static float SERVER_NETWORK_INTERVAL = 1 / 20f;

    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem() {
        super(Aspect.getAspectForAll(Position.class, Velocity.class), SERVER_NETWORK_INTERVAL);
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            int connection = world.getManager(ServerPlayerManager.class).getConnectionByPlayer(entity);
            if (connection != -1) {
                ServerManager serverManager = world.getManager(ServerManager.class);
                serverManager.network.sendObjectUDP(connection, new PositionPacket(entity, 0));
            }
        }
    }
}
