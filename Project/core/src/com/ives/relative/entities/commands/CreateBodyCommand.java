package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.TileManager;

/**
 * Created by Ives on 14/12/2014.
 */
public class CreateBodyCommand extends Command {

    public CreateBodyCommand() {

    }

    public CreateBodyCommand(boolean simulate) {
        super(simulate);
    }


    @Override
    public void execute(final Entity entity) {
        entity.getWorld().getManager(TileManager.class).createTile(entity.getWorld().getManager(PlanetManager.class).getPlanet("earth"),
                20, 20, 0, "dirt", true);
    }
}
