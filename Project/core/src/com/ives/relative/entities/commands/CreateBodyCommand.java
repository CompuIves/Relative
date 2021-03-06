package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.universe.planets.TileManager;
import com.ives.relative.utils.ComponentUtils;

/**
 * Created by Ives on 14/12/2014.
 *
 * Creates a dirt tile above the executor
 */
public class CreateBodyCommand extends Command {
    Entity body;

    public CreateBodyCommand() {
        super(false);
    }

    @Override
    public void executeDown(final Entity entity) {
        Vector2 position = entity.getWorld().getMapper(Physics.class).get(entity).body.getPosition();
        Location location = entity.getWorld().getMapper(Location.class).get(entity);
        body = entity.getWorld().getManager(TileManager.class).createTile(location.space, position.x, position.y + 4, 0, "dirt", true);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                entity.getWorld().getSystem(NetworkSendSystem.class).sendEntityToAll(body);
            }
        });
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
        return true;
    }
}
