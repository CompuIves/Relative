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
import com.ives.relative.managers.CommandSystem;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.updates.PositionPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 *
 * This is the network system which receives all input packets and processes it, as you can see the server doesn't have
 * to send update packets every frame. We assume that te client is almost always synchronous with the server.
 */
@Wire
public class ServerNetworkSystem extends IntervalEntitySystem {
    public final static float SERVER_NETWORK_INTERVAL = 1 / 20f;
    private final ServerNetwork network;
    private final Map<Long, Integer> lastInputsReceived;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<NetworkC> mNetworkC;
    protected CommandSystem commandManager;

    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), SERVER_NETWORK_INTERVAL);
        lastInputsReceived = new HashMap<Long, Integer>();
        this.network = network;

        processRequests();
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (Entity entity : entities) {
            if (mPosition.get(entity) != null)
                sendPositions(entity);
        }
    }

    public void processRequests() {
        network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof UpdatePacket) {
                    if (object instanceof CommandPacket) {
                        CommandPacket packet = (CommandPacket) object;
                        processInput(packet);
                    }
                    if (object instanceof CommandPressPacket) {
                        CommandPressPacket packet = (CommandPressPacket) object;
                        processInput(packet);
                    }
                }
            }
        });
    }

    public void processInput(CommandPacket packet) {
        lastInputsReceived.put(packet.entityID, packet.sequence);
        System.out.println("Stored sequence: " + packet.sequence + " for " + packet.entityID);
        for (int i = 0; i < packet.inputsPressed.length; i++) {
            byte command = packet.inputsPressed[i];
            commandManager.commandDown(command, packet.entityID, false);
        }

        for (int i = 0; i < packet.inputsReleased.length; i++) {
            byte command = packet.inputsReleased[i];
            commandManager.commandUp(command, packet.entityID, false);
        }
    }

    public void processInput(CommandPressPacket packet) {
        lastInputsReceived.put(packet.entityID, packet.sequence);
        if (packet.pressed)
            commandManager.commandDown(packet.command, packet.entityID, false);
        else
            commandManager.commandUp(packet.command, packet.entityID, false);
    }

    public void sendPositions(Entity entity) {
        Position position = mPosition.get(entity);
        if (position.py != position.y || position.px != position.x) {
            //If there is a change in position;
            long id = mNetworkC.get(entity).id;
            int sequence = -1;
            if (lastInputsReceived.containsKey(id)) {
                sequence = lastInputsReceived.get(id);
            }
            network.sendObjectUDPToAll(new PositionPacket(entity, sequence, id));
        }
    }

}
