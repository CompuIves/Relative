package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.core.packets.updates.CommandPacket;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.network.NetworkC;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ClientNetworkSystem extends IntervalEntitySystem {
    public static float CLIENT_NETWORK_INTERVAL = 1 / 60f;
    protected ClientManager clientManager;

    Array<Byte> commandList;
    Array<Float> deltaTimeList;
    int sequence;

    public ClientNetworkSystem(ClientNetwork network) {
        super(Aspect.getAspectForAll(NetworkC.class, Position.class), CLIENT_NETWORK_INTERVAL);
        commandList = new Array<Byte>();
        deltaTimeList = new Array<Float>();

        network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
            }
        });
    }

    public void addCommand(float delta, Command command) {
        if (commandList.size == 0) {
            commandList.add(command.getID());
            deltaTimeList.add(0f);
        } else {
            commandList.add(command.getID());
            deltaTimeList.add(delta);
        }
    }

    /**
     * Entitylist is empty because I gave no aspect, we don't need an entitylist
     *
     * @param entities empty
     */
    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        if (commandList.size != 0) {
            int connectionID = ClientNetwork.CONNECTIONID;
            long entityID = world.getManager(ClientManager.class).playerNetworkID;
            CommandPacket commandPacket = new CommandPacket(sequence, commandList, deltaTimeList, entityID);
            clientManager.network.sendObjectTCP(connectionID, commandPacket);
            commandList.clear();
            sequence++;
        }
    }
}
