package com.ives.relative.entities.commands;

/**
 * Created by Ives on 14/12/2014.
 */
public class DoNothingCommand extends Command {
    public DoNothingCommand(byte commandID) {
        super(commandID);
    }

    @Override
    public byte getID() {
        return commandID;
    }
}
