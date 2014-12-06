package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Entity;

/**
 * Created by Ives on 5/12/2014.
 */
public class Command {
    public void execute(Entity entity) {}

    /**
     * Do the opposite of execute! For example when the key is released.
     * @param entity the entity which should be acted upon
     */
    public void antiExecute(Entity entity) {}
}
