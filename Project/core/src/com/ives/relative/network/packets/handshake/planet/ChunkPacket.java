package com.ives.relative.network.packets.handshake.planet;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.Chunk;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 7/1/2015.
 * Contains chunks
 * HANDLED BY CLIENT
 */
public class ChunkPacket extends ResponsePacket {
    Chunk chunk;

    public ChunkPacket() {
    }

    public ChunkPacket(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                ChunkManager chunkManager = game.world.getManager(ChunkManager.class);
                chunk.setLoaded(false);
                chunkManager.addChunk(chunk);
            }
        });
    }
}
