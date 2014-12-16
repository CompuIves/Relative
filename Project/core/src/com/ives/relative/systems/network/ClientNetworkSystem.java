package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.commands.DoNothingCommand;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.CommandSystem;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.ClientMove;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandPacket;
import com.ives.relative.network.packets.input.HookDownCommandPacket;
import com.ives.relative.network.packets.input.HookUpCommandPacket;
import com.ives.relative.network.packets.requests.RequestEntity;
import com.ives.relative.network.packets.updates.PositionPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ClientNetworkSystem extends IntervalEntitySystem {
    public static float CLIENT_NETWORK_INTERVAL = 1 / 20f;
    public long playerNetworkId;
    protected ClientManager clientManager;
    protected CommandSystem commandManager;
    protected NetworkManager networkManager;

    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    /**
     * This contains every byte of the command, this will be sent to the server
     */
    Array<Byte> commandDownNetworkList;
    Array<Byte> commandUpNetworkList;
    Map<Integer, ClientMove> sentCommands;
    int sequence;
    private Array<Float> deltaTimeList;

    public ClientNetworkSystem(ClientNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), CLIENT_NETWORK_INTERVAL);
        commandDownNetworkList = new Array<Byte>();
        commandUpNetworkList = new Array<Byte>();
        deltaTimeList = new Array<Float>();


        sentCommands = new HashMap<Integer, ClientMove>();
        processRequests(network);

    }

    public void sendDownCommand(Command command, float delta) {
        if (command instanceof DoNothingCommand)
            return;

        if (command.hook) {
            if (!command.isHooked) {
                clientManager.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new HookDownCommandPacket(sequence, playerNetworkId, commandManager.getID(command)));
                command.isHooked = true;
            }
        } else {
            commandDownNetworkList.add(commandManager.getID(command.getClass().getSimpleName()));
            deltaTimeList.add(delta);
        }
    }

    public void sendUpCommand(Command command) {
        if (command instanceof DoNothingCommand)
            return;

        if (command.hook) {
            clientManager.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new HookUpCommandPacket(sequence, playerNetworkId, commandManager.getID(command)));
            command.isHooked = false;
        } else {
            //TODO Make use of this.
            commandUpNetworkList.add(commandManager.getID(command.getClass().getSimpleName()));
        }
    }

    public void registerPlayer(long playerNetworkId) {
        this.playerNetworkId = playerNetworkId;
    }

    public long getPlayerID() {
        return playerNetworkId;
    }

    /**
     * Sends the input of the player to the server, also puts it in a local variable to know how to apply Server
     * Reconciliation.
     * @param entities
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        if (commandDownNetworkList.size != 0) {
            int connectionID = ClientNetwork.CONNECTIONID;
            long entityID = this.playerNetworkId;
            CommandPacket commandPacket = new CommandPacket(sequence, commandDownNetworkList, deltaTimeList, entityID);
            clientManager.network.sendObjectUDP(connectionID, commandPacket);

            commandDownNetworkList.clear();
            deltaTimeList.clear();
            sequence++;
        }
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
                                if (!processPosition(packet)) {
                                    network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(((PositionPacket) object).entityID));
                                }
                                //applyServerReconciliation(packet);
                            }
                        });
                    }
                }
            }
        });
    }

    public boolean processPosition(PositionPacket packet) {
        Entity entity = networkManager.getNetworkEntity(packet.entityID);
        //networkEntity.edit().add(position).add(velocity);
        if (entity != null) {
            float x = packet.x;
            float y = packet.y;
            float vx = packet.vx;
            float vy = packet.vy;
            float rotation = packet.rotation;

            Position localPosition = entity.getWorld().getMapper(Position.class).get(entity);
            Velocity localVelocity = entity.getWorld().getMapper(Velocity.class).get(entity);
            Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);

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
    public void applyServerReconciliation(UpdatePacket packet) {
        //Server Reconciliation
        if (packet.entityID == playerNetworkId) {
            sentCommands.remove(packet.sequence);
            System.out.println("Removed packet: " + packet.sequence);

            for(Map.Entry entry : sentCommands.entrySet()) {
                int localSequence = (Integer) entry.getKey();

                /*
                if(localSequence <= sequence) {
                    System.out.println("Applied reconciliation for: " + entry.getKey());
                    ClientMove clientMove = (ClientMove) entry.getValue();
                    Entity entity = networkManager.getNetworkEntity(playerNetworkId);
                    Physics physics = mPhysics.get(entity);
                    Position position = mPosition.get(entity);
                    Velocity velocity = mVelocity.get(entity);

                    Body body = physics.body;
                    //body.setLinearVelocity(clientMove.vx, clientMove.vy);
                    System.out.println("ClientMove, x: " + clientMove.x + " y: " + clientMove.y);
                    body.setTransform(clientMove.x, clientMove.y, body.getAngle());

                    position.x = clientMove.x;
                    position.y = clientMove.y;
                    velocity.vx = clientMove.vx;
                    velocity.vy = clientMove.vy;
                }
                */
            }

        }
    }
}
