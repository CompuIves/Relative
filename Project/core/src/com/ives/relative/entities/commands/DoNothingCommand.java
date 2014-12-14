package com.ives.relative.entities.commands;

import com.artemis.Entity;

/**
 * Created by Ives on 14/12/2014.
 */
public class DoNothingCommand extends Command {


    public DoNothingCommand(byte commandID, boolean simulate) {
        super(commandID, simulate);
    }

    @Override
    public byte getID() {
        return commandID;
    }

    @Override
    public void execute(Entity entity) {

    }
}
