package com.ives.relative.systems;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.ives.relative.entities.commands.Command;
import com.ives.relative.managers.CommandManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

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
    protected UuidEntityManager uuidEntityManager;

    Multimap<UUID, Command> hookedCommands;
    Multimap<Byte, UUID> hookedEntities;

    public CommandSystem() {
        hookedCommands = ArrayListMultimap.create();
        hookedEntities = ArrayListMultimap.create();
    }

    @Override
    protected void processSystem() {
        Iterator<Map.Entry<UUID, Command>> it = hookedCommands.entries().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            Entity e = uuidEntityManager.getEntity((UUID) entry.getKey());

            if (e != null) {
                Command command = (Command) entry.getValue();
                if (command.canExecute(e))
                    command.whilePressed(e);
            } else {
                hookedEntities.remove(entry.getValue(), entry.getKey());
                it.remove();
            }
        }
    }

    public void commandDown(byte command, Entity e) {
        commandDown(commandManager.getCommand(command), e);
    }

    public void commandDown(Command command, Entity e) {
        if (e == null)
            return;

        if (hookedCommands.containsKey(uuidEntityManager.getUuid(e)) && hookedEntities.containsKey(commandManager.getID(command)))
            return;

        hookedCommands.put(uuidEntityManager.getUuid(e), command);
        hookedEntities.put(commandManager.getID(command), uuidEntityManager.getUuid(e));

        if (command.canExecute(e)) {
            //Always activate the command just in case it is allowed to be executed later, but check for keydown.
            command.keyDown(e);
        }
    }

    public void commandUp(byte command, Entity e) {
        if (e == null)
            return;

        if (hookedEntities.containsKey(command) && hookedCommands.containsKey(uuidEntityManager.getUuid(e))) {
            Collection<Command> commands = hookedCommands.get(uuidEntityManager.getUuid(e));

            Command oldCommand = null;
            for (Command c : commands) {
                if (commandManager.getID(c) == command) {
                    if (c.canExecute(e)) {
                        //Always remove command, but check if up can be executed.
                        c.keyUp(e);
                    }
                    oldCommand = c;
                    commandManager.freeCommand(oldCommand);
                }
            }

            hookedCommands.remove(uuidEntityManager.getUuid(e), oldCommand);
            hookedEntities.remove(command, uuidEntityManager.getUuid(e));
        }
    }
}
