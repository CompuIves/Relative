package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.ives.relative.entities.commands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 14/12/2014.
 * <p/>
 * Since the client has an {@link com.ives.relative.entities.components.client.InputC} and the server hasn't the server
 * needs to have a database of which ID is bound to which command. Sending commands over the network has proven to produce
 * too much bandwidth so now we use IDs.
 */
public class CommandManager extends Manager {
    Map<Byte, Command> commandMap;
    Map<String, Byte> idMap;

    public CommandManager() {
        commandMap = new CommandsMap<Byte, Command>(new DoNothingCommand(false));
        idMap = new HashMap<String, Byte>();
    }

    public void addCommand(Command command) {
        byte id = (byte) commandMap.size();
        commandMap.put(id, command);
        idMap.put(command.getClass().getSimpleName(), id);
    }

    @Override
    protected void initialize() {
        addCommand(new MoveLeftCommand(true));
        addCommand(new MoveRightCommand(true));
        addCommand(new JumpCommand(true));
        addCommand(new CreateBodyCommand(true));
    }

    public void executeCommand(byte commandID, Entity e) {
        commandMap.get(commandID).handle(e, false);
    }

    /**
     * Get command ID by simple name
     *
     * @param command command.getClass().getSimpleName()
     * @return ID
     */
    public Byte getID(String command) {
        return idMap.get(command);
    }
}
