package com.ives.relative.systems;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.managers.CommandManager;
import com.ives.relative.managers.NetworkManager;

import java.util.Collection;
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
    protected NetworkManager networkManager;
    protected CommandManager commandManager;

    Multimap<Long, Command> hookedCommands;
    Multimap<Byte, Long> hookedEntities;

    public CommandSystem() {
        hookedCommands = ArrayListMultimap.create();
        hookedEntities = ArrayListMultimap.create();
    }

    @Override
    protected void processSystem() {
        for (Map.Entry entry : hookedCommands.entries()) {
            Entity e = networkManager.getEntity((Long) entry.getKey());
            //System.out.println("EntityID = " + e.getId());
            Command command = (Command) entry.getValue();
            command.whilePressed(e);
        }
    }

    public void commandDown(byte command, long e) {
        commandDown(commandManager.getCommand(command), e);
    }

    public void commandDown(Command command, long entityID) {
        if (hookedCommands.containsKey(entityID) && hookedEntities.containsKey(commandManager.getID(command)))
            return;

        hookedCommands.put(entityID, command);
        hookedEntities.put(commandManager.getID(command), entityID);
        command.keyDown(networkManager.getEntity(entityID), false);
    }

    public void commandUp(byte command, long entityID) {
        if (hookedEntities.containsKey(command) && hookedCommands.containsKey(entityID)) {
            Collection<Command> commands = hookedCommands.get(entityID);

            Command oldCommand = null;
            for (Command c : commands) {
                if (commandManager.getID(c) == command) {
                    c.keyUp(networkManager.getEntity(entityID), false);
                    oldCommand = c;
                    commandManager.freeCommand(oldCommand);
                }
            }

            hookedCommands.remove(entityID, oldCommand);
            hookedEntities.remove(command, entityID);
        }
    }
}
