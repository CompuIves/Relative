package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public abstract class Command {
    /**
     * Hooked commands will be executed every frame (moving for example), until it's told not to.
     */
    public boolean hook;
    /**
     * If this is a hook variable it needs to say if it is already hooked to the network, otherwise
     * the client network system will send it too many times.
     */
    public boolean isHooked;
    boolean simulate;

    /**
     * Creates a new command
     *
     * @param simulate Should it be executed on the client before getting a reaction from the server?
     * @param hook     Should this command be executed every frame when activated?
     */
    public Command(boolean simulate, boolean hook) {
        this.simulate = simulate;
        this.hook = hook;
    }

    public void keyDown(final Entity entity, boolean send, final float delta) {
        if (send) {
            entity.getWorld().getSystem(ClientNetworkSystem.class).sendDownCommand(this, delta);

            if (simulate) {
                executeDown(entity, delta);
            }
        } else {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    executeDown(entity, delta);
                }
            });
        }
    }

    public void keyUp(final Entity e, boolean send) {
        if (send) {
            e.getWorld().getSystem(ClientNetworkSystem.class).sendUpCommand(this);

            if (simulate)
                executeUp(e);
        } else {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    executeUp(e);
                }
            });
        }
    }

    public abstract void executeDown(Entity e, float delta);

    public abstract void executeUp(Entity e);
}
