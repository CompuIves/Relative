package com.ives.relative.entities.commands;

import com.artemis.Entity;

/**
 * Created by Ives on 14/12/2014.
 */
public class DoNothingCommand extends Command {


    /**
     * This command does literally nothing.
     */
    public DoNothingCommand() {
        super(false);
    }

    @Override
    public void executeDown(Entity entity) {

    }

    @Override
    public void execute(Entity e, float delta) {

    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public boolean canExecute(Entity e) {
        return true;
    }
}
