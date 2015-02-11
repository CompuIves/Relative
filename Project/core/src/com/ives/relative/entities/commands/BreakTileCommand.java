package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 1/1/2015.
 */
public class BreakTileCommand extends ClickCommand {

    /**
     * A empty constructor for cloning, the cursor position needs to be set afterwards however.
     */
    public BreakTileCommand() {
        super(true);
    }


    @Override
    void executeDown(Entity e) {
        Chunk chunk = e.getWorld().getMapper(Position.class).get(e).chunk;
        if (worldPosClicked != null)
            e.getWorld().getManager(TileManager.class).removeTile(chunk, worldPosClicked);
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
}
