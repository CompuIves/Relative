package com.ives.relative.network.packets.handshake.planet;

import com.badlogic.gdx.Gdx;
import com.ives.relative.core.GameManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;

/**
 * Created by Ives on 7/1/2015.
 * Contains chunk
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
                chunkManager.transferChunk(chunk);
            }
        });
    }
}
