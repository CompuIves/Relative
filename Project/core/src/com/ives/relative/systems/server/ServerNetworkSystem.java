package com.ives.relative.systems.server;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.commands.ClickCommand;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandClickPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.updates.DeltaPositionPacket;
import com.ives.relative.network.packets.updates.PositionHeartbeat;
import com.ives.relative.network.packets.updates.PositionPacket;
import com.ives.relative.network.packets.updates.RemoveTilePacket;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Iterator;
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
    public final static float SERVER_NETWORK_INTERVAL = 1 / 10f;
    private final ServerNetwork network;
    private final Map<Integer, Integer> lastInputsReceived;
    private final Map<Integer, Float> lastReturnTripTime;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<NetworkC> mNetworkC;
    protected CommandSystem commandSystem;
    protected CommandManager commandManager;
    protected NetworkManager networkManager;
    private BlockingQueue<UpdatePacket> packetQueue;

    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), SERVER_NETWORK_INTERVAL);
        lastInputsReceived = new HashMap<Integer, Integer>();
        lastReturnTripTime = new HashMap<Integer, Float>();
        packetQueue = new LinkedBlockingQueue<UpdatePacket>();
        this.network = network;
        processRequests();
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
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
        /*
        for (Entity entity : entities) {
            if (mPosition.get(entity) != null)
                sendPositions(entity);
        }
        */
        Iterator<Map.Entry<Integer, Float>> it = lastReturnTripTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Float> entry = it.next();
            entry.setValue(entry.getValue() - SERVER_NETWORK_INTERVAL);
            if (entry.getValue() < 0) {
                it.remove();
            }
        }

        for (int i = 0; i < packetQueue.size(); i++) {
            try {
                UpdatePacket updatePacket = packetQueue.take();
                if (updatePacket instanceof CommandPressPacket) {
                    CommandPressPacket packet = (CommandPressPacket) updatePacket;
                    processInput(packet);
                } else if (updatePacket instanceof PositionHeartbeat) {
                    PositionHeartbeat packet = (PositionHeartbeat) updatePacket;
                    processHeartbeat(packet);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processRequests() {
        network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof UpdatePacket) {
                    lastInputsReceived.put(((UpdatePacket) object).entityID, ((UpdatePacket) object).sequence);
                    if (object instanceof CommandPressPacket) {
                        final CommandPressPacket packet = (CommandPressPacket) object;
                        //Add the packet to the queue for processing.
                        //packetQueue.add(packet);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                processInput(packet);
                            }
                        });

                    } else if (object instanceof PositionHeartbeat) {
                        PositionHeartbeat packet = (PositionHeartbeat) object;
                        //Remove old packet
                        packetQueue.remove(packet);
                        packetQueue.add(packet);
                    }
                }
            }
        });
    }

    public void processInput(CommandPressPacket packet) {
        Entity e = networkManager.getEntity(packet.entityID);
        if (e == null)
            return;

        Command c;
        if (packet.pressed) {
            if (packet instanceof CommandClickPacket) {
                CommandClickPacket clickPacket = (CommandClickPacket) packet;
                c = commandManager.getCommand(packet.command);
                ((ClickCommand) c).setWorldPosClicked(new Vector2(clickPacket.x, clickPacket.y));
                if (c.canExecute(e)) {
                    //TODO change the hardcoded earth
                    network.sendObjectUDPToAll(new RemoveTilePacket(clickPacket.x, clickPacket.y, "earth"));
                }
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

    public void processHeartbeat(PositionHeartbeat packet) {
        Entity e = networkManager.getEntity(packet.entityID);
        if (e != null) {
            float posOffset = 0.5f;
            float posDOffset = 0.2f;
            Position position = mPosition.get(e);
            Velocity velocity = mVelocity.get(e);
            float dx = Math.abs(packet.x - position.x);
            float dy = Math.abs(packet.y - position.y);
            if (dx > posOffset || dy > posOffset) {
                System.out.println("dx: " + dx + " dy: " + dy);
                System.out.println("Entity PosHeartbeat rejected! ID: " + e.getId());
                network.sendObjectTCP(packet.connection, new PositionPacket(lastInputsReceived.get(packet.entityID), packet.entityID, position.x, position.y, position.rotation,
                        velocity.vx, velocity.vy, velocity.vr, true));
            } else {
                System.out.println("Entity PosHeartbeat accepted! DX: " + dx);

                if (dx > posDOffset || dy > posDOffset) {
                    if (!lastReturnTripTime.containsKey(packet.entityID)) {
                        network.sendObjectTCP(packet.connection, new DeltaPositionPacket(lastInputsReceived.get(packet.entityID), packet.entityID, position.x - packet.x, position.y - packet.y));
                        System.out.println(ServerNetwork.getConnection(packet.connection).getReturnTripTime());
                        ServerNetwork.getConnection(packet.connection).updateReturnTripTime();
                        lastReturnTripTime.put(packet.entityID, ServerNetwork.getConnection(packet.connection).getReturnTripTime() / 1000f);
                    }
                }
            }


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
