package com.ives.relative.managers.server;

import com.artemis.Entity;
import com.artemis.Manager;
import com.ives.relative.entities.commands.*;

import java.util.Map;

/**
 * Created by Ives on 14/12/2014.
 * <p/>
 * Since the client has an {@link com.ives.relative.entities.components.client.InputC} and the server hasn't the server
 * needs to have a database of which ID is bound to which command. Sending commands over the network has proven to produce
 * too much bandwidth so now we use IDs.
 */
public class ServerCommandManager extends Manager {
    Map<Integer, Command> commandMap;

    public ServerCommandManager() {
        commandMap = new CommandsMap<Integer, Command>(new DoNothingCommand((byte) 0, false));
        commandMap.put(1, new MoveLeftCommand((byte) 1, true));
        commandMap.put(2, new MoveRightCommand((byte) 2, true));
        commandMap.put(3, new JumpCommand((byte) 3, true));
        commandMap.put(4, new CreateBodyCommand((byte) 4, false));
    }

    public void executeCommand(int commandID, Entity e) {
        commandMap.get(commandID).handle(e);
    }
}
