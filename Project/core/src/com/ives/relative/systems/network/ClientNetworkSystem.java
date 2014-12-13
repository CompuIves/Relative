package com.ives.relative.systems.network;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.IntervalEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.core.packets.updates.CommandPacket;
import com.ives.relative.entities.commands.Command;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class ClientNetworkSystem extends IntervalEntitySystem {
    public static float CLIENT_NETWORK_INTERVAL = 1 / 20f;
    protected ClientManager clientManager;

    Array<Command> commandList;
    Array<Float> deltaTimeList;
    int sequence;

    public ClientNetworkSystem() {
        super(Aspect.getEmpty(), CLIENT_NETWORK_INTERVAL);
        commandList = new Array<Command>();
        deltaTimeList = new Array<Float>();
    }

    public void addCommand(float delta, Command command) {
        if (commandList.size == 0) {
            commandList.add(command);
            deltaTimeList.add(0f);
        } else {
            commandList.add(command);
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
            int entityID = world.getManager(TagManager.class).getEntity("player").getId();
            CommandPacket commandPacket = new CommandPacket(sequence, commandList, deltaTimeList, entityID);
            clientManager.network.sendObjectUDP(connectionID, commandPacket);
            commandList.clear();
            sequence++;
        }
    }
}
