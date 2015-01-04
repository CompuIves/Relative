package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.entities.commands.ClickCommand;
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
import com.ives.relative.network.packets.input.CommandClickPacket;
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

    int sequence;
    int frame = 0;
    private int playerNetworkId;
    private Client client;
    private Map<Object, Object> simulatedPositions;
    private Array<Integer> requestedEntities;

    public ClientNetworkSystem(ClientNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), CLIENT_NETWORK_INTERVAL);

        client = (Client) network.endPoint;
        client.updateReturnTripTime();

        requestedEntities = new Array<Integer>();

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

        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandPressPacket(sequence, playerNetworkId, commandManager.getID(command), true));
        sequence++;
        client.updateReturnTripTime();
    }

    public void sendClickCommand(ClickCommand clickCommand) {
        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandClickPacket(sequence, playerNetworkId, commandManager.getID(clickCommand), true, clickCommand.getWorldPosClicked()));
        sequence++;
    }

    /**
     * Sends the up command to the server
     *
     * @param command just the command which needs to be sent, can be a dummy command if needed
     */
    public void sendUpCommand(Command command) {
        if (command instanceof DoNothingCommand)
            return;
        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandPressPacket(sequence, playerNetworkId, commandManager.getID(command), false));
        sequence++;
    }

    public void sendUnClickCommand(ClickCommand clickCommand) {
        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandClickPacket(sequence, playerNetworkId, commandManager.getID(clickCommand), false, clickCommand.getWorldPosClicked()));
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
                                        if (!requestedEntities.contains(packet.entityID, true)) {
                                            network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(packet.entityID));
                                            requestedEntities.add(packet.entityID);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    if (object instanceof ComponentPacket) {
                        ComponentPacket packet = (ComponentPacket) object;
                        networkReceiveSystem.addDataForProcessing(packet);
                        requestedEntities.removeValue(packet.entityID, true);
                    }
                }
            }
        });
    }

    /**
     * This is a sort of Server Reconciliation, it checks if the position sent by the server was right a ReturnTripTime ago.
     * Because the server always sends outdated information (sometimes 600ms old) this system checks if the information was
     * right ping time ago.
     *
     * @param packet
     * @return
     */
    private boolean checkForPrevious(PositionPacket packet) {
        //TODO The ping can vary greatly, there needs to be a way to make the check somewhat less precise but still right.

        float x = packet.x;
        float y = packet.y;
        float offset = 1f;

        int timeFrame = frame - Math.round(((client.getReturnTripTime() / 1000f) / CLIENT_NETWORK_INTERVAL));

        Position oldPosition = (Position) simulatedPositions.get(timeFrame);

        if (oldPosition == null)
            return false;

        float dx = Math.abs(x - oldPosition.x);
        float dy = Math.abs(y - oldPosition.y);
        if (dx > offset || dy > offset) {
            System.out.println("Not accepted: " + client.getReturnTripTime() + " dx: " + dx + " dy: " + dy);
            System.out.println("Frame: " + frame + " OldFrame: " + timeFrame);
            System.out.println("Previous frame x : " + Math.abs(x - ((Position) simulatedPositions.get(timeFrame - 5)).x));
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
            Vector2 bodyPos = body.getTransform().getPosition();
            if (bodyPos.x != x || bodyPos.y != y) {
                body.setTransform(x, y, rotation);
                localPosition.x = x;
                localPosition.y = y;
            }

            Vector2 bodyVel = body.getLinearVelocity();
            if (bodyVel.x != vx || bodyVel.y != vy) {
                body.setLinearVelocity(vx, vy);
                localVelocity.vx = vx;
                localVelocity.vy = vy;
            }

            return true;
        } else {
            return false;
        }
    }
}
