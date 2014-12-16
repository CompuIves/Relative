package com.ives.relative.entities.commands;

import com.artemis.Entity;

/**
 * Created by Ives on 14/12/2014.
 */
public class DoNothingCommand extends Command {


    /**
     * This command literally does nothing.
     */
    public DoNothingCommand() {
        super(false, false);
    }

    @Override
    public void executeDown(Entity entity, float delta) {

    }

    @Override
    public void executeUp(Entity e) {

    }
}
