package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.managers.planet.TileManager;

/**
 * Created by Ives on 1/1/2015.
 */
public class BreakTileCommand extends ClickCommand {

    /**
     * A empty constructor for cloning, the cursor position needs to be set afterwards however.
     */
    public BreakTileCommand() {
        super(true, null);
    }

    /**
     * Creates a new command
     */
    public BreakTileCommand(Vector2 worldPosClicked) {
        super(true, worldPosClicked);
    }

    @Override
    void executeDown(Entity e) {
        String planet = e.getWorld().getMapper(Position.class).get(e).worldID;
        if (worldPosClicked != null)
            e.getWorld().getManager(TileManager.class).removeTile(worldPosClicked, planet);
    }

    @Override
    void execute(Entity e, float delta) {

    }

    @Override
    void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public boolean canExecute(Entity e) {
        Position position = e.getWorld().getMapper(Position.class).get(e);
        Vector2 pos = new Vector2(position.x, position.y);
        float dx = Math.abs(pos.x - getWorldPosClicked().x);
        float dy = Math.abs(pos.y - getWorldPosClicked().y);
        return dx + dy < 4;
    }

    @Override
    public Command clone() {
        return new BreakTileCommand();
    }
}
