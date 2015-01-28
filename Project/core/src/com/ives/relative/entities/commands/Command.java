package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;

/**
 * Created by Ives on 5/12/2014.
 * A command can be applied to an entity. This can vary greatly from removing an entity to letting an entity jump. The
 * big advantage of this is that you can perform commands on entities using inputs or AI. See Game Programming Patterns:
 * Command.
 */
public abstract class Command {
    boolean simulate;
    float startTime;
    float deltaTime;

    /**
     * Creates a new command
     *
     * @param simulate Should it be executed on the client before getting a reaction from the server?
     */
    public Command(boolean simulate) {
        this.simulate = simulate;
    }

    /**
     * Methods which will be called from other classes
     *
     * @param e entity
     */
    public void keyDown(Entity e) {
        startTime = System.nanoTime();
        executeDown(e);
    }

    public void whilePressed(Entity e) {
        execute(e, Gdx.graphics.getDeltaTime());
    }

    public void keyUp(Entity e) {
        deltaTime = System.nanoTime() - startTime;
        executeUp(e, deltaTime);
    }

    /**
     * Gets called when the button is pressed
     *
     * @param e The entity this will be acted upon
     */
    abstract void executeDown(Entity e);

    /**
     * Gets executed while the button is pressed
     *
     * @param e     The entity this will be executed on
     * @param delta The time between this frame and last frame, this makes commands for things like breaking blocks
     *              much more manageable to implement.
     */
    abstract void execute(Entity e, float delta);

    /**
     * Gets executed when the button is released
     *
     * @param e     The entity this will be executed on
     * @param delta The presstime of the button
     */
    abstract void executeUp(Entity e, float delta);

    /**
     * Undo the command
     */
    public abstract void undo();

    /**
     * Is the entity allowed to execute the command?
     *
     * @return if the entity is allowed to execute the command
     */
    public abstract boolean canExecute(Entity e);

    @Override
    public boolean equals(Object obj) {
        return this.getClass().getSimpleName().equals(obj.getClass().getSimpleName());
    }


    public void reset() {
        startTime = 0;
        deltaTime = 0;
    }

    public boolean isSimulate() {
        return simulate;
    }
}
