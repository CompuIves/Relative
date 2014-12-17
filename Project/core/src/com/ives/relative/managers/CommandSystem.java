package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ives.relative.entities.commands.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 14/12/2014.
 * <p/>
 * Since the client has an {@link com.ives.relative.entities.components.client.InputC} and the server hasn't the server
 * needs to have a database of which ID is bound to which command. Sending commands over the network has proven to produce
 * too much bandwidth so now we use IDs.
 */
@Wire
public class CommandSystem extends VoidEntitySystem {
    Map<Byte, Command> commandMap;
    Map<String, Byte> idMap;

    Multimap<Entity, Command> hookedCommands;
    Multimap<Byte, Entity> hookedEntities;

    public CommandSystem() {
        commandMap = new CommandsMap<Byte, Command>(new DoNothingCommand());
        idMap = new HashMap<String, Byte>();

        hookedCommands = ArrayListMultimap.create();
        hookedEntities = ArrayListMultimap.create();
    }

    @Override
    protected void processSystem() {
        for (Map.Entry entry : hookedCommands.entries()) {
            Entity e = (Entity) entry.getKey();
            //System.out.println("EntityID = " + e.getId());
            Command command = (Command) entry.getValue();
            command.whilePressed(e);
        }
    }

    @Override
    protected void initialize() {
        addCommand(new DoNothingCommand());
        addCommand(new MoveLeftCommand());
        addCommand(new MoveRightCommand());
        addCommand(new JumpCommand());
        addCommand(new CreateBodyCommand());
    }

    public void addCommand(Command command) {
        byte id = (byte) commandMap.size();
        commandMap.put(id, command);
        idMap.put(command.getClass().getSimpleName(), id);
    }


    public Command getCommand(byte command) {
        return commandMap.get(command);
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

    public Byte getID(Command command) {
        return idMap.get(command.getClass().getSimpleName());
    }

    public void commandDown(byte command, Entity e, boolean send) {
        commandDown(getCommand(command).clone(), e, send);
    }

    public void commandDown(Command command, Entity e, boolean send) {
        if (hookedCommands.containsKey(e) && hookedEntities.containsKey(getID(command)))
            return;

        hookedCommands.put(e, command);
        hookedEntities.put(getID(command), e);
        command.keyDown(e, send);
    }

    public void commandUp(byte command, Entity e, boolean send) {
        if (hookedEntities.containsKey(command) && hookedCommands.containsKey(e)) {
            Collection<Command> commands = hookedCommands.get(e);

            Command oldCommand = null;
            for (Command c : commands) {
                if (getID(c) == command) {
                    c.keyUp(e, send);
                    oldCommand = c;
                }
            }


            hookedCommands.remove(e, oldCommand);
            hookedEntities.remove(command, e);
        }
    }
}
