package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.TileSystem;
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
    public void executeDown(Entity entity) {
        body = entity.getWorld().getSystem(TileSystem.class).createTile(entity.getWorld().getManager(PlanetManager.class).getPlanet("earth"),
                20, 20, 0, "dirt", true);
    }

    @Override
    public void execute(Entity e, float delta) {

    }

    @Override
    public void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {
        ComponentUtils.removeEntity(body);
    }

    @Override
    public void reset() {
        super.reset();
        this.body = null;
    }

    @Override
    public boolean canExecute(Entity e) {
        //Returns whether the world of the body is locked
        return true;
    }

    @Override
    public Command clone() {
        return new CreateBodyCommand();
    }
}
