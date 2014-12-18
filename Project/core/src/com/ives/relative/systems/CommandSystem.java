package com.ives.relative.systems;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.managers.CommandManager;

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
    protected CommandManager commandManager;

    Multimap<Entity, Command> hookedCommands;
    Multimap<Byte, Entity> hookedEntities;

    public CommandSystem() {
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

    public void commandDown(byte command, Entity e) {
        commandDown(commandManager.getCommand(command), e);
    }

    public void commandDown(Command command, Entity e) {
        if (hookedCommands.containsKey(e) && hookedEntities.containsKey(commandManager.getID(command)))
            return;

        hookedCommands.put(e, command);
        hookedEntities.put(commandManager.getID(command), e);
        command.keyDown(e, false);
    }

    public void commandUp(byte command, Entity e) {
        if (hookedEntities.containsKey(command) && hookedCommands.containsKey(e)) {
            Collection<Command> commands = hookedCommands.get(e);

            Command oldCommand = null;
            for (Command c : commands) {
                if (commandManager.getID(c) == command) {
                    c.keyUp(e, false);
                    oldCommand = c;
                    commandManager.freeCommand(oldCommand);
                }
            }

            hookedCommands.remove(e, oldCommand);
            hookedEntities.remove(command, e);
        }
    }
}
