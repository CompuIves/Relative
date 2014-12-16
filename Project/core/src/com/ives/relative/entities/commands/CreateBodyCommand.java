package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.TileManager;

/**
 * Created by Ives on 14/12/2014.
 */
public class CreateBodyCommand extends Command {

    public CreateBodyCommand() {
        super(false, false);
    }

    @Override
    public void executeDown(final Entity entity, float delta) {

    }

    @Override
    public void executeUp(Entity e) {
        e.getWorld().getManager(TileManager.class).createTile(e.getWorld().getManager(PlanetManager.class).getPlanet("earth"),
                20, 20, 0, "dirt", true);
    }
}
