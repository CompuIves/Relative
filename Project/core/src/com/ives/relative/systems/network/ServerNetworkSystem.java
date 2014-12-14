package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.server.ServerCommandManager;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.input.CommandPacket;
import com.ives.relative.network.packets.updates.PositionPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ServerNetworkSystem extends IntervalEntitySystem {
    public final static float SERVER_NETWORK_INTERVAL = 1 / 20f;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<NetworkC> mNetworkC;
    private ServerNetwork network;

    private Map<Long, Integer> lastInputsReceived;

    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class), SERVER_NETWORK_INTERVAL);
        lastInputsReceived = new HashMap<Long, Integer>();
        this.network = network;

        processRequests(network);
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            if (mPosition.get(entity) != null)
                sendPositions(entity);
        }
    }

    public void processRequests(Network network) {
        network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof BasePacket) {
                    if (object instanceof CommandPacket) {
                        CommandPacket packet = (CommandPacket) object;
                        processInput(packet);
                    }
                }
            }
        });
    }

    public void processInput(CommandPacket packet) {
        lastInputsReceived.put(packet.entityID, packet.sequence);
        Entity entity = world.getManager(NetworkManager.class).getNetworkEntity(packet.entityID);
        for (byte command : packet.commandList) {
            world.getManager(ServerCommandManager.class).executeCommand(command, entity);
        }
    }

    public void sendPositions(Entity entity) {
        Position position = mPosition.get(entity);
        if (position.py != position.y || position.px != position.x) {
            long id = mNetworkC.get(entity).id;
            network.sendObjectUDPToAll(new PositionPacket(entity, 0, id));
        }
    }
}
