package com.ives.relative.entities.commands;


import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 5/12/2014.
 */
public class Command {
    public void execute(Entity entity) {
        if (entity.getWorld().getSystem(ClientNetworkSystem.class) != null)
            entity.getWorld().getSystem(ClientNetworkSystem.class).addCommand(Gdx.graphics.getDeltaTime(), this);
    }
}
