package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

/**
 * Created by Ives on 5/12/2014.
 */
public abstract class Command {
    public void execute(Entity entity) {}
}
