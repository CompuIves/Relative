package com.ives.relative.managers;

import com.artemis.Manager;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.commands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 18/12/2014.
 * <p/>
 * The database of every command linked to its ID.
 * It's also a Command Factory. (Search Pool Factory)
 */
public class CommandManager extends Manager {
    final Map<Byte, Command> commandMap;
    final Map<String, Byte> idMap;

    final Map<String, Array<Command>> freeCommands;

    public CommandManager() {
        commandMap = new HashMap<Byte, Command>();
        idMap = new HashMap<String, Byte>();
        freeCommands = new HashMap<String, Array<Command>>();

        initialize();
    }

    /**
     * Standard initialization, adds all commands to the database
     */
    protected void initialize() {
        addCommand(new DoNothingCommand());
        addCommand(new MoveLeftCommand());
        addCommand(new MoveRightCommand());
        addCommand(new JumpCommand());
        addCommand(new CreateBodyCommand());
        addCommand(new BreakTileCommand());
    }

    /**
     * Add a command to the database, this doesn't support removing commands
     *
     * @param command command to be added
     */
    protected void addCommand(Command command) {
        byte id = (byte) commandMap.size();
        commandMap.put(id, command);
        idMap.put(command.getClass().getSimpleName(), id);
    }


    /**
     * Get command by ID
     *
     * @param id the id
     * @return the command
     */
    public Command getCommand(byte id) {
        Command command = commandMap.get(id);
        return getCommand(command.getClass());
    }

    /**
     * Get a new command by the command given. The command given will only be an identifier for the factory, the factory
     * will search for a free command. If there is no free command the factory will copy the command given and return it.
     *
     * @param command sort of template
     * @return a new command
     */
    public Command getCommand(Command command) {
        return getCommand(command.getClass());
    }


    /**
     * Get the desired command, this command will be generated or called from a list called FreeCommands.
     *
     * @param command The class of the command which needs to be returned
     * @return The command which needs to be returned
     */
    public Command getCommand(Class<? extends Command> command) {
        //If the command is in the free command pool
        if (freeCommands.containsKey(command.getSimpleName())) {
            Command freeCommand = freeCommands.get(command.getSimpleName()).first();
            freeCommand.reset();
            return freeCommands.get(command.getSimpleName()).first();
        } else {
            try {
                return command.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //Shouldn't happen with command.newInstance()
        return null;
    }

    /**
     * This command is done being used. It will be reset for recycling.
     *
     * @param command The command to be freed
     */
    public void freeCommand(Command command) {
        if (freeCommands.containsKey(command.getClass().getSimpleName())) {
            freeCommands.get(command.getClass().getSimpleName()).add(command);
        } else {
            Array<Command> commands = new Array<Command>();
            commands.add(command);
            freeCommands.put(command.getClass().getSimpleName(), commands);
        }
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


}
