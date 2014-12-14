package com.ives.relative.core.packets.updates;

import com.artemis.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.server.ServerCommandManager;

/**
 * Created by Ives on 11/12/2014.
 * This packet contains the input in the given timestep.
 * <p/>
 * SENT BY CLIENT
 * HANDLED BY SERVER
 */
public class CommandPacket extends UpdatePacket {
    byte[] commandList;
    //float[] deltaTimeList;
    long entityID;

    public CommandPacket() {
    }

    public CommandPacket(int sequence, Array<Byte> commandList, Array<Float> deltaTimeList, long entityID) {
        super(sequence);
        this.commandList = new byte[commandList.size];
        //this.deltaTimeList = new float[deltaTimeList.size];

        for (int i = 0; i < commandList.size; i++) {
            this.commandList[i] = commandList.get(i);
            //this.deltaTimeList[i] = deltaTimeList.get(i);
        }
        this.entityID = entityID;
    }

    @Override
    public void response(GameManager game) {
        Entity entity = game.world.getManager(NetworkManager.class).getNetworkEntity(entityID);
        for (byte command : commandList) {
            game.world.getManager(ServerCommandManager.class).executeCommand(command, entity);
        }
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("ServerNetwork", "Command list size: " + commandList.length);
    }
}
