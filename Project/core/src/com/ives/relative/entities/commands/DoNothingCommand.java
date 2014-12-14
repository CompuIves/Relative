package com.ives.relative.entities.commands;

import com.artemis.Entity;

/**
 * Created by Ives on 14/12/2014.
 */
public class DoNothingCommand extends Command {


    public DoNothingCommand(boolean simulate) {
        super(simulate);
    }

    @Override
    public void execute(Entity entity) {

    }
}
