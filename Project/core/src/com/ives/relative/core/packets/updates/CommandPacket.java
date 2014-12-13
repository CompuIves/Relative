package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.commands.Command;

/**
 * Created by Ives on 11/12/2014.
 * This packet contains the input in the given timestep.
 * <p/>
 * SENT BY CLIENT
 * HANDLED BY SERVER
 */
public class CommandPacket extends UpdatePacket {
    Command[] commandList;
    float[] deltaTimeList;
    int entityID;

    public CommandPacket() {
    }

    public CommandPacket(int sequence, Array<Command> commandList, Array<Float> deltaTimeList, int entityID) {
        super(sequence);
        this.commandList = new Command[commandList.size];
        this.deltaTimeList = new float[deltaTimeList.size];

        for (int i = 0; i < commandList.size; i++) {
            this.commandList[i] = commandList.get(i);
            this.deltaTimeList[i] = deltaTimeList.get(i);
        }
        this.entityID = entityID;
    }

    @Override
    public void response(GameManager game) {
        Entity entity = game.world.getEntity(entityID);
        for (Command command : commandList) {
            command.execute(entity);
        }
    }
}
