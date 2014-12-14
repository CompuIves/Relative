package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.TileManager;

/**
 * Created by Ives on 14/12/2014.
 */
public class CreateBodyCommand extends Command {

    public CreateBodyCommand(byte commandID) {
        super(commandID);
    }

    @Override
    public byte getID() {
        return commandID;
    }

    @Override
    public void execute(Entity entity) {
        super.execute(entity);
        System.out.println("Executed bodycommand");
        entity.getWorld().getManager(TileManager.class).createTile(entity.getWorld().getManager(PlanetManager.class).getPlanet("earth"),
                20, 20, 0, "dirt", true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
