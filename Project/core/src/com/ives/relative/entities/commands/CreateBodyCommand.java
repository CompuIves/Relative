package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.TileManager;
import com.ives.relative.utils.ComponentUtils;

/**
 * Created by Ives on 14/12/2014.
 */
public class CreateBodyCommand extends Command {
    Entity body;

    public CreateBodyCommand() {
        super(false);
    }

    @Override
    public void executeDown(final Entity entity) {

    }

    @Override
    public void execute(Entity e) {

    }

    @Override
    public void executeUp(Entity e, float delta) {
        body = e.getWorld().getManager(TileManager.class).createTile(e.getWorld().getManager(PlanetManager.class).getPlanet("earth"),
                20, 20, 0, "dirt", true);
    }

    @Override
    public void undo() {
        ComponentUtils.removeEntity(body);
    }

    @Override
    public boolean canExecute(Entity e) {
        //Returns whether the world of the body is locked
        return e.getWorld().getMapper(WorldC.class).get(e.getWorld().getManager(PlanetManager.class).getPlanet("earth")).world.isLocked();
    }

    @Override
    public Command clone() {
        return new CreateBodyCommand();
    }
}
