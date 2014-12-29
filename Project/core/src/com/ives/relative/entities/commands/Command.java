package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.ives.relative.systems.client.ClientNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
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
     * @param e    entity
     * @param send should this be sent to the server?
     */
    public void keyDown(Entity e, boolean send) {
        startTime = System.nanoTime();

        if (send) {
            e.getWorld().getSystem(ClientNetworkSystem.class).sendDownCommand(this);

            if (simulate) {
                executeDown(e);
            }
        } else {
            executeDown(e);
        }
    }

    public void whilePressed(Entity e) {
        execute(e);
    }

    public void keyUp(Entity e, boolean send) {
        deltaTime = System.nanoTime() - startTime;
        if (send) {
            e.getWorld().getSystem(ClientNetworkSystem.class).sendUpCommand(this);

            if (simulate) {
                executeUp(e, deltaTime);
            }
        } else {
            executeUp(e, deltaTime);

        }
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
     * @param e The entity this will be executed on
     */
    abstract void execute(Entity e);

    /**
     * Gets executed when the button is released
     *
     * @param e     The entity this will be executed on
     * @param delta The presstime of the button
     */
    abstract void executeUp(Entity e, float delta);

    @Override
    public boolean equals(Object obj) {
        return this.getClass().getSimpleName().equals(obj.getClass().getSimpleName());
    }

    /**
     * If a new command needs to be made it can be done using this clone method, will return
     * a new version of the last command.
     *
     * @return Desired command
     */
    public abstract Command clone();


    public void reset() {
        startTime = 0;
        deltaTime = 0;
    }

}
