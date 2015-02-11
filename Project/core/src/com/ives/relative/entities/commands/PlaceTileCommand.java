package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.planets.TileManager;
import com.ives.relative.utils.RelativeMath;

/**
 * Created by Ives on 11/2/2015.
 */
public class PlaceTileCommand extends ClickCommand {

    public PlaceTileCommand() {
        super(true);
    }

    @Override
    void executeDown(Entity e) {

        UniverseBody ub = e.getWorld().getMapper(Position.class).get(e).universeBody;
        Chunk chunk = e.getWorld().getManager(ChunkManager.class).getTopChunk(ub, worldPosClicked);
        if (worldPosClicked != null) {
            if (!chunk.isTile(RelativeMath.fastfloor(worldPosClicked.x), RelativeMath.fastfloor(worldPosClicked.y))) {
                System.out.println("exec");
                Entity t = e.getWorld().getManager(TileManager.class).createTile(chunk.universeBody,
                        RelativeMath.fastfloor(worldPosClicked.x),
                        RelativeMath.fastfloor(worldPosClicked.y),
                        0, "dirt", false);
                chunk.addTile(RelativeMath.fastfloor(worldPosClicked.x), RelativeMath.fastfloor(worldPosClicked.y), e.getWorld().getManager(UuidEntityManager.class).getUuid(t));
                e.getWorld().getManager(TileManager.class).generateTileBodies(chunk);
            }
        }

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
        return true;
    }
}
