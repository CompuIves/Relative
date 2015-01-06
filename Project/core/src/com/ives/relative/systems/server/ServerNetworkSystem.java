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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.entities.commands.ClickCommand;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandClickPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.updates.GrantEntitiesAuthority;
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
    public final static float SERVER_NETWORK_INTERVAL = 1 / 60f;
    private final ServerNetwork network;
    private final Map<Integer, Integer> lastInputsReceived;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<NetworkC> mNetworkC;
    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Physics> mPhysics;

    protected CommandSystem commandSystem;
    protected CommandManager commandManager;
    protected NetworkManager networkManager;
    protected AuthorityManager authorityManager;
    private BlockingQueue<UpdatePacket> packetQueue;


    /**
     * Creates a new IntervalEntitySystem.
     */
    public ServerNetworkSystem(ServerNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), SERVER_NETWORK_INTERVAL);
        lastInputsReceived = new HashMap<Integer, Integer>();
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


        for (int i = 0; i < packetQueue.size(); i++) {
            try {
                UpdatePacket updatePacket = packetQueue.take();
                if (updatePacket instanceof CommandPressPacket) {
                    CommandPressPacket packet = (CommandPressPacket) updatePacket;
                    processInput(packet);
                }
                if (updatePacket instanceof PositionPacket) {
                    PositionPacket packet = (PositionPacket) updatePacket;
                    if (authorityManager.isEntityAuthorizedByConnection(packet.connection, packet.entityID)) {
                        Entity e = networkManager.getEntity(packet.entityID);
                        processPosition(e, packet);
                        network.sendObjectUDPToAll(new PositionPacket(e, 0, packet.entityID));
                    }
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
                    lastInputsReceived.put(((UpdatePacket) object).connection, ((UpdatePacket) object).sequence);
                    if (object instanceof CommandPressPacket) {
                        final CommandPressPacket packet = (CommandPressPacket) object;
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                processInput(packet);
                            }
                        });

                    }
                    if (object instanceof PositionPacket) {
                        packetQueue.add((UpdatePacket) object);
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

    public void processPosition(Entity entity, PositionPacket packet) {
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

    public void sendAuthorization(int connection, Array<Integer> entityIDs) {
        if (entityIDs.size != 0) {
            network.sendObjectTCP(connection, new GrantEntitiesAuthority(0, entityIDs));
        }
    }

    public void sendAuthorization(int connection, Entity e) {
        int id = networkManager.getNetworkID(e);
        Array<Integer> entity = new Array<Integer>();
        if (id != -1) {
            entity.add(id);
            network.sendObjectTCP(connection, new GrantEntitiesAuthority(0, entity));
        }
    }
}
