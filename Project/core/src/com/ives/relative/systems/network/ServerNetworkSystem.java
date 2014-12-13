package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.ives.relative.core.packets.updates.PositionPacket;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ServerNetworkSystem extends IntervalEntitySystem {
    public final static float SERVER_NETWORK_INTERVAL = 1 / 20f;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<NetworkC> mNetworkC;
    ServerNetwork network;

    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class, Velocity.class), SERVER_NETWORK_INTERVAL);
        this.network = network;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            int connection = world.getManager(ServerPlayerManager.class).getConnectionByPlayer(entity);
            if (connection != -1) {
                Position position = mPosition.get(entity);
                long id = mNetworkC.get(entity).id;
                if (position.py != position.y || position.px != position.x) {
                    network.sendObjectUDPToAll(new PositionPacket(entity, 0, id));
                }
            }
        }
    }
}
