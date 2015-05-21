package com.ives.relative.network.packets.updates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 3/1/2015.
 */
public class RemoveTilePacket extends ResponsePacket {
    float x, y;
    String universeBody;

    public RemoveTilePacket() {
        super();
    }

    public RemoveTilePacket(float x, float y, String universeBody) {
        this.x = x;
        this.y = y;
        this.universeBody = universeBody;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Space u = game.world.getSystem(UniverseManager.class).getSpace(universeBody);
                Chunk chunk = game.world.getManager(ChunkManager.class).getChunk(u, new Vector2(x, y));
                game.world.getManager(TileManager.class).removeTile(chunk, new Vector2(x, y));
            }
        });
    }
}
