package com.ives.relative.managers;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
    protected void initialize() {
        addCommand(new DoNothingCommand());
        addCommand(new MoveLeftCommand());
        addCommand(new MoveRightCommand());
        addCommand(new JumpCommand());
        addCommand(new CreateBodyCommand());
    }

    @Override
    protected void processSystem() {
        for (Map.Entry entry : hookedCommands.entries()) {
            Entity e = (Entity) entry.getKey();
            Command command = (Command) entry.getValue();
            command.keyDown(e, false, world.getDelta());
            System.out.println("Button is pressed: " + command.getClass().getSimpleName());
        }
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

    public void executeCommand(byte commandID, Entity e, float delta) {
        commandMap.get(commandID).keyDown(e, false, delta);
        commandMap.get(commandID).keyUp(e, false);
    }


    /**
     * Hooks a command, hooked commands will be executed every timestep
     *
     * @param entity  entity to be hooked to
     * @param command command that has to be hooked
     */
    public void hookCommand(Entity entity, Command command) {
        hookCommand(entity, getID(command));
    }

    public void hookCommand(Entity entity, byte command) {
        if (hookedCommands.containsKey(entity) && hookedEntities.containsKey(command))
            return;

        hookedCommands.put(entity, getCommand(command));
        hookedEntities.put(command, entity);
    }

    public void unHookCommand(Entity entity, byte command) {
        if (hookedEntities.containsKey(command) && hookedCommands.containsKey(entity)) {
            hookedCommands.remove(entity, getCommand(command));
            hookedEntities.remove(command, entity);
        }
    }
}
