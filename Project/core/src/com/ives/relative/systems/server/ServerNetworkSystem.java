package com.ives.relative.systems.server;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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
import com.ives.relative.network.packets.UpdatePacket;
import com.ives.relative.network.packets.input.CommandClickPacket;
import com.ives.relative.network.packets.input.CommandPressPacket;
import com.ives.relative.network.packets.updates.GrantEntityAuthority;
import com.ives.relative.network.packets.updates.PositionPacket;
import com.ives.relative.network.packets.updates.RemoveTilePacket;
import com.ives.relative.systems.CommandSystem;

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
        packetQueue = new LinkedBlockingQueue<UpdatePacket>();
        this.network = network;
        processRequests();
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for (int i = 0; i < packetQueue.size(); i++) {
            try {
                UpdatePacket updatePacket = packetQueue.take();
                if (updatePacket instanceof CommandPressPacket) {
                    CommandPressPacket packet = (CommandPressPacket) updatePacket;
                    processInput(packet);
                }
                if (updatePacket instanceof PositionPacket) {
                    PositionPacket packet = (PositionPacket) updatePacket;
                    Entity e = networkManager.getEntity(packet.entityID);
                    //TODO bad null check.
                    if (e != null) {
                        if (authorityManager.isEntityAuthorizedByPlayer(packet.connection, e)) {
                            processPosition(e, packet);
                            network.sendObjectUDPToAllExcept(packet.connection, new PositionPacket(e, 0, packet.entityID, packet.connection));
                        } else {
                            //If the client doesn't have authority it has to be corrected
                            network.sendObjectUDP(packet.connection, new PositionPacket(e, 0, packet.entityID, packet.connection));
                        }
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
            body.setTransform(x, y, rotation);
            body.setLinearVelocity(vx, vy);
            localPosition.x = x;
            localPosition.y = y;
            localVelocity.vx = vx;
            localVelocity.vy = vy;
            localVelocity.vr = rVelocity;
            body.setAngularVelocity(rVelocity);
        }
    }

    public void sendAuthority(int connection, Entity e, AuthorityManager.AuthorityType type) {
        network.sendObjectTCP(connection, new GrantEntityAuthority(0, networkManager.getNetworkID(e), type));
    }
}
