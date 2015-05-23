package com.ives.relative.entities.commands;

import com.artemis.Entity;
import com.artemis.managers.UuidEntityManager;
import com.ives.relative.entities.components.body.Location;
import com.ives.relative.universe.Space;
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
        Space ub = e.getWorld().getMapper(Location.class).get(e).space;
        Chunk chunk = e.getWorld().getManager(ChunkManager.class).getChunk(ub, worldPosClicked);
        if (worldPosClicked != null) {
            if (!chunk.isTile(RelativeMath.fastfloor(worldPosClicked.x), RelativeMath.fastfloor(worldPosClicked.y))) {
                System.out.println("exec");
                Entity t = e.getWorld().getManager(TileManager.class).createTile(chunk.space,
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
