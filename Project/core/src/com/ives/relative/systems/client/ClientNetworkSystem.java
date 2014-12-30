package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.commands.DoNothingCommand;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.requests.RequestEntity;
import com.ives.relative.network.packets.updates.ComponentPacket;
import com.ives.relative.network.packets.updates.PositionPacket;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ClientNetworkSystem extends IntervalEntitySystem {
    public static float CLIENT_NETWORK_INTERVAL = 1 / 60f;
    protected ClientManager clientManager;
    protected CommandManager commandManager;
    protected NetworkManager networkManager;
    protected NetworkReceiveSystem networkReceiveSystem;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Physics> mPhysics;
    /**
     * This contains every byte of the command, this will be sent to the server
     */
    Array<Byte> commandDownNetworkList;
    Array<Byte> commandUpNetworkList;
    int sequence;
    int frame = 0;
    private int playerNetworkId;
    private Client client;
    private Multimap<Integer, Command> sentCommands;
    private Map<Object, Object> simulatedPositions;

    public ClientNetworkSystem(ClientNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), CLIENT_NETWORK_INTERVAL);
        commandDownNetworkList = new Array<Byte>();
        commandUpNetworkList = new Array<Byte>();

        client = (Client) network.endPoint;
        client.updateReturnTripTime();

        sentCommands = ArrayListMultimap.create();
        simulatedPositions = createFIFOMap(((int) (1 / CLIENT_NETWORK_INTERVAL)));
        processRequests(network);
    }

    public static <K, V> Map<K, V> createFIFOMap(final int maxEntries) {
        return new LinkedHashMap<K, V>(maxEntries * 3 / 2, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        };
    }

    public void sendDownCommand(Command command) {
        if (command instanceof DoNothingCommand)
            return;
        commandDownNetworkList.add(commandManager.getID(command.getClass().getSimpleName()));
        sentCommands.put(sequence, command);

        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandPressPacket(sequence, playerNetworkId, commandManager.getID(command), true));
        sequence++;

        client.updateReturnTripTime();
    }

    public void sendUpCommand(Command command) {
        if (command instanceof DoNothingCommand)
            return;
        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandPressPacket(sequence, playerNetworkId, commandManager.getID(command), false));
        sequence++;
    }

    public void registerPlayer(int playerNetworkId) {
        this.playerNetworkId = playerNetworkId;
    }

    public Entity getPlayer() {
        return networkManager.getEntity(playerNetworkId);
    }

    public int getPlayerID() {
        return playerNetworkId;
    }

    /**
     * Sends the input of the player to the server, also puts it in a local variable to know how to apply Server
     * Reconciliation.
     * @param entities Entities to process
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        Entity player = networkManager.getEntity(playerNetworkId);
        if (player != null) {
            Position p = mPosition.get(player);
            float x = p.x;
            float y = p.y;
            simulatedPositions.put(frame, new Position(x, y, 0, 0, ""));
        }

        frame++;
    }

    public void processRequests(final Network network) {
        network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, final Object object) {
                if (object instanceof UpdatePacket) {
                    if (object instanceof PositionPacket) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                PositionPacket packet = (PositionPacket) object;
                                if (packet.entityID == playerNetworkId) {
                                    if (!checkForPrevious(packet)) {
                                        processPosition(packet);
                                    }
                                } else {
                                    if (!processPosition(packet)) {
                                        network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(((PositionPacket) object).entityID));
                                    }
                                }
                                //applyServerReconciliation(packet);
                            }
                        });
                    }

                    if (object instanceof ComponentPacket) {
                        ComponentPacket packet = (ComponentPacket) object;
                        networkReceiveSystem.addDataForProcessing(packet);
                    }
                }
            }
        });
    }

    private boolean checkForPrevious(PositionPacket packet) {
        float x = packet.x;
        float y = packet.y;
        float offset = 0.5f;

        int timeFrame = (int) (frame - ((client.getReturnTripTime() / 1000f) / CLIENT_NETWORK_INTERVAL));

        Position oldPosition = (Position) simulatedPositions.get(timeFrame);

        if (oldPosition == null)
            return false;

        float dx = x - oldPosition.x;
        float dy = y - oldPosition.y;
        if (dx < 0) {
            if (dx < -offset) {
                return false;
            }
        } else if (dx > offset) {
            return false;
        }
        if (dy < 0) {
            if (dy < -offset) {
                return false;
            }
        } else if (dy > offset) {
            return false;
        }

        return true;
    }

    public boolean processPosition(PositionPacket packet) {
        Entity entity = networkManager.getEntity(packet.entityID);
        //networkEntity.edit().add(position).add(velocity);
        if (entity != null) {
            float x = packet.x;
            float y = packet.y;
            float vx = packet.vx;
            float vy = packet.vy;
            float rotation = packet.rotation;

            Position localPosition = mPosition.get(entity);
            Velocity localVelocity = mVelocity.get(entity);
            Physics physics = mPhysics.get(entity);

            Body body = physics.body;
            body.setTransform(x, y, rotation);
            localPosition.x = x;
            localPosition.y = y;

            body.setLinearVelocity(vx, vy);
            localVelocity.vx = vx;
            localVelocity.vy = vy;

            return true;
        } else {
            return false;
        }
    }


    /**
     * This applies sent input which hasn't yet been processed by the server. Purely keeps it locally smooth.
     *
     * @param packet The packet which has been received.
     */
    /*
    public void applyServerReconciliation(UpdatePacket packet) {
        //System.out.println("PLAYERNETWORKID: " + playerNetworkId);
        //System.out.println("PACKETENTITYID: " + packet.entityID);
        //Server Reconciliation
        if (packet.entityID == playerNetworkId) {
            Entity entity = getPlayer();
            sentCommands.removeAll(packet.sequence);
            //System.out.println("Removed packet: " + packet.sequence);
            //System.out.println("Sequence size now: " + sentCommands.size());
            for (Map.Entry entry : sentCommands.entries()) {
                int localSequence = (Integer) entry.getKey();
                System.out.println("Looking at packet with: " + packet.sequence + " and trying " + localSequence);
                if (localSequence > packet.sequence) {
                    System.out.println("Executed entry with localSeq: " + localSequence + " and seq: " + sequence);
                    Command command = (Command) entry.getValue();
                    command.applyReconciliation(entity);
                }
            }
            for (Command command : inputSystem.commandsActivated) {
                command.applyReconciliation(entity);
            }
        }

    }
    */
}
