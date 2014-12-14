package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public abstract class Command {
    boolean simulate;

    public Command() {
    }

    public Command(boolean simulate) {
        this.simulate = simulate;
    }

    public void handle(final Entity entity, boolean send) {
        if (send) {
            entity.getWorld().getSystem(ClientNetworkSystem.class).addCommand(this);

            if (simulate) {
                execute(entity);
            }
        } else {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    execute(entity);
                }
            });
        }
    }

    public abstract void execute(Entity e);
}
