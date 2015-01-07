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
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.MovementEvent;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandClickPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.requests.RequestEntity;
import com.ives.relative.network.packets.updates.ComponentPacket;
import com.ives.relative.network.packets.updates.DeltaPositionPacket;
import com.ives.relative.network.packets.updates.GrantEntityAuthority;
import com.ives.relative.network.packets.updates.PositionPacket;

import java.util.Iterator;

/**
 * Created by Ives on 13/12/2014.
 * This is the system which handles networked commands and movement. This system communicates with the server and updatees
 * every entity accordingly.
 */
@Wire
public class ClientNetworkSystem extends IntervalEntitySystem implements EntityEventObserver {
    public static float CLIENT_NETWORK_INTERVAL = 1 / 10f;
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
    private Array<Integer> requestedEntities;
    private Array<Integer> grantedEntities;
    private Array<Integer> entitiesToSend;

    private Array<BasePacket> packetsToBeProcessed;

    public ClientNetworkSystem(ClientNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), CLIENT_NETWORK_INTERVAL);
        requestedEntities = new Array<Integer>();
        grantedEntities = new Array<Integer>();
        entitiesToSend = new Array<Integer>();

        packetsToBeProcessed = new Array<BasePacket>();
        processRequests(network);
    }

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    public void sendDownCommand(Command command) {
        if (command instanceof DoNothingCommand)
            return;

        clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, new CommandPressPacket(sequence, playerNetworkId, commandManager.getID(command), true));
        sequence++;
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
        frame++;
        for (int entity : entitiesToSend) {
            Entity e = networkManager.getEntity(entity);
            PositionPacket positionPacket = new PositionPacket(e, sequence, entity);
            clientManager.network.sendObjectUDP(ClientNetwork.CONNECTIONID, positionPacket);
        }
        entitiesToSend.clear();

        Iterator<BasePacket> it = packetsToBeProcessed.iterator();
        while (it.hasNext()) {
            BasePacket basePacket = it.next();
            if (basePacket instanceof GrantEntityAuthority) {
                if (processAuthority((GrantEntityAuthority) basePacket)) {
                    it.remove();
                }
            }
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
                                if (packet.force && !grantedEntities.contains(packet.entityID, true)) {
                                    if (!processPosition(packet)) {
                                        network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(packet.entityID));
                                        requestedEntities.add(packet.entityID);
                                    }
                                }
                                /*
                                if (packet.entityID == playerNetworkId) {

                                } else {
                                    if (!processPosition(packet)) {
                                        if (!requestedEntities.contains(packet.entityID, true)) {
                                            network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(packet.entityID));
                                            requestedEntities.add(packet.entityID);
                                        }
                                    }
                                }
                                */
                            }
                        });
                    }
                    if (object instanceof DeltaPositionPacket) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                processDeltaPosition((DeltaPositionPacket) object);
                            }
                        });
                    }

                    if (object instanceof ComponentPacket) {
                        ComponentPacket packet = (ComponentPacket) object;
                        networkReceiveSystem.addDataForProcessing(packet);
                        requestedEntities.removeValue(packet.entityID, true);
                    }
                }
                if (object instanceof GrantEntityAuthority) {
                    if (!processAuthority((GrantEntityAuthority) object)) {
                        packetsToBeProcessed.add((BasePacket) object);
                    }
                }
            }
        });
    }

    public boolean processAuthority(GrantEntityAuthority packet) {
        Entity entity = networkManager.getEntity(packet.entity);
        System.out.println(packet.entity);
        if (entity != null) {
            if (packet.type == AuthorityManager.AuthorityType.PERMANENT) {
                Physics p = mPhysics.get(entity);
                AuthorityManager.addProximitySensor(p);
            }
            grantedEntities.add(packet.entity);
            return true;
        } else {
            return false;
        }
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
            float rVelocity = packet.vr;

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

            body.setAngularVelocity(rVelocity);

            return true;
        }
        return false;
    }

    public boolean processDeltaPosition(DeltaPositionPacket packet) {
        Entity e = networkManager.getEntity(packet.entityID);
        if (e != null) {
            float dx = packet.dx;
            float dy = packet.dy;

            Position p = mPosition.get(e);
            Physics physics = mPhysics.get(e);

            Body body = physics.body;
            body.applyLinearImpulse(new Vector2(dx * body.getMass() * 4, dy * body.getMass() * 4), body.getPosition(), true);
            return true;
        }
        return false;
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {
        if (event instanceof MovementEvent) {
            int id = networkManager.getNetworkID(e);
            if (!entitiesToSend.contains(id, true)) {
                entitiesToSend.add(id);
            }
        }
    }
}
