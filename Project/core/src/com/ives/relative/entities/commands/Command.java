package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public abstract class Command {
    byte commandID;
    boolean simulate;

    public Command(byte commandID, boolean simulate) {
        this.commandID = commandID;
        this.simulate = simulate;
    }

    public abstract byte getID();

    public void handle(Entity entity) {
        if (entity.getWorld().getSystem(ClientNetworkSystem.class) != null) {
            entity.getWorld().getSystem(ClientNetworkSystem.class).addCommand(Gdx.graphics.getDeltaTime(), this);

            if (simulate)
                execute(entity);
        } else {
            execute(entity);
        }
    }

    public abstract void execute(Entity e);
}
