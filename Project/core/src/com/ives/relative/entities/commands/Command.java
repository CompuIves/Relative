package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.systems.network.ClientNetworkSystem;

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

    public void keyDown(final Entity e, boolean send) {
        if (send) {
            e.getWorld().getSystem(ClientNetworkSystem.class).sendDownCommand(this);

            if (simulate) {
                startRecord(e);
                executeDown(e);
            }
        } else {
            startRecord(e);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    executeDown(e);
                }
            });
        }
    }

    public void whilePressed(final Entity e) {
        whileRecord(e);
        execute(e);
    }

    public void keyUp(final Entity e, boolean send) {
        if (send) {
            e.getWorld().getSystem(ClientNetworkSystem.class).sendUpCommand(this);

            if (simulate) {
                endRecord(e);
                executeUp(e, deltaTime);
            }
        } else {
            endRecord(e);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    executeUp(e, deltaTime);
                }
            });
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

    /**
     * Starts recording the components from the moment the key is pressed.
     */
    void startRecord(Entity e) {
        startTime = System.nanoTime();
    }

    /**
     * Sometimes there also needs to be a check while applying input, for example while moving (increasing deltax)
     *
     * @param e
     */
    void whileRecord(Entity e) {
        float curTime = System.nanoTime();
        deltaTime = curTime - startTime;
    }

    /**
     * Converts the changed states into deltaComponents -> Components with only changed values.
     */
    void endRecord(Entity e) {
        float endTime = System.nanoTime();
        deltaTime = endTime - startTime;
    }

    /**
     * Replicates the button press to reconciliate between client and server
     *
     * @param e Entity to apply to
     */
    public void applyReconciliation(Entity e) {
        if (deltaTime != 0) {
            executeDown(e);
            executeUp(e, deltaTime);
        }
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
