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
        super(false);
    }

    @Override
    public void executeDown(Entity entity) {

    }

    @Override
    public void execute(Entity e) {

    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    /**
     * Don't return a new instance, DoNothingCommand is just garbage :D
     *
     * @return
     */
    @Override
    public Command clone() {
        return this;
    }
}
