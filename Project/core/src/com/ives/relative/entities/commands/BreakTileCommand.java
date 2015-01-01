package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.managers.TileSystem;

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
        if (worldPosClicked != null)
        e.getWorld().getSystem(TileSystem.class).removeTile(worldPosClicked);
    }

    @Override
    void execute(Entity e) {

    }

    @Override
    void executeUp(Entity e, float delta) {

    }

    @Override
    public void undo() {

    }

    @Override
    public boolean canExecute(Entity e) {
        return true;
    }

    @Override
    public Command clone() {
        return new BreakTileCommand();
    }
}
