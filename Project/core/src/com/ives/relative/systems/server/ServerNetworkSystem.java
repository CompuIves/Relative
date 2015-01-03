package com.ives.relative.systems.server;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.commands.ClickCommand;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandClickPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.updates.PositionPacket;
import com.ives.relative.network.packets.updates.RemoveTilePacket;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private final Map<Integer, Integer> lastInputsReceived;

    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<NetworkC> mNetworkC;
    protected CommandSystem commandSystem;
    protected CommandManager commandManager;
    protected NetworkManager networkManager;


    BlockingQueue<CommandPressPacket> packetQueue;
    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), SERVER_NETWORK_INTERVAL);
        lastInputsReceived = new HashMap<Integer, Integer>();
        this.network = network;

        packetQueue = new LinkedBlockingQueue<CommandPressPacket>();

        processRequests();
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {

        //Process inputs in the main thread, this will solve concurrent issues.
        for (int i = 0; i < packetQueue.size(); i++) {
            try {
                CommandPressPacket packet = packetQueue.take();
                processInput(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Array<Entity> players = world.getManager(ServerPlayerManager.class).getPlayers();
        for (Entity player : players) {
            Position playerPos = mPosition.get(player);

            for (Entity e : entities) {
                Position entityPos = mPosition.get(e);
                NetworkC networkC = mNetworkC.get(e);

                float dx = Math.abs(playerPos.x - entityPos.x);
                float dy = Math.abs(playerPos.y - entityPos.y);
                int priority = networkC.priority;
            }
        }
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
                    if (object instanceof CommandPressPacket) {
                        CommandPressPacket packet = (CommandPressPacket) object;
                        //Add the packet to the queue for processing.
                        packetQueue.add(packet);
                    }
                }
            }
        });
    }

    public void processInput(CommandPressPacket packet) {
        lastInputsReceived.put(packet.entityID, packet.sequence);
        Entity e = networkManager.getEntity(packet.entityID);
        if (e == null)
            return;

        Command c;
        if (packet.pressed) {
            if (packet instanceof CommandClickPacket) {
                CommandClickPacket clickPacket = (CommandClickPacket) packet;
                c = commandManager.getCommand(packet.command);
                ((ClickCommand) c).setWorldPosClicked(new Vector2(clickPacket.x, clickPacket.y));
                if (c.canExecute(e))
                    network.sendObjectUDPToAll(new RemoveTilePacket(clickPacket.x, clickPacket.y));
            } else {
                c = commandManager.getCommand(packet.command);
            }
            commandSystem.commandDown(c, e);
        } else {
            //TODO don't have to send worldPos with unclick and should also send packets when moving the mouse.
            if (packet instanceof CommandClickPacket) {
                CommandClickPacket clickPacket = (CommandClickPacket) packet;
                c = commandManager.getCommand(packet.command);
                ((ClickCommand) c).setWorldPosClicked(new Vector2(clickPacket.x, clickPacket.y));
            } else {
                c = commandManager.getCommand(packet.command);
            }
            commandSystem.commandUp(commandManager.getID(c), e);
        }
    }

    public void sendPositions(Entity entity) {
        if (checkChangePos(entity)) {
            //If there is a change in position;
            int id = mNetworkC.get(entity).id;
            int sequence = -1;
            if (lastInputsReceived.containsKey(id)) {
                sequence = lastInputsReceived.get(id);
            }
            network.sendObjectUDPToAll(new PositionPacket(entity, sequence, id));
        }
    }

    public boolean checkChangePos(Entity entity) {
        Position position = mPosition.get(entity);
        return position.py != position.y || position.px != position.x;
    }

    public void manualHashCheck(Entity e) {
        Array<Component> components = ComponentUtils.getComponents(e);
        Array<Integer> hashCodes = new Array<Integer>();

        for (Component c : components) {
            hashCodes.add(c.hashCode());
        }
    }
}
