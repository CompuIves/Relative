package com.ives.relative.network.packets.handshake.planet;

import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.planet.Chunk;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;

import java.util.UUID;

/**
 * Created by Ives on 11/1/2015.
 * Says to the server the planet is received and the server can send more info.
 */
public class ReceivedPlanet extends ResponsePacket {
    public ReceivedPlanet() {
    }

    @Override
    public void response(GameManager game) {
        ChunkManager chunkManager = game.world.getManager(ChunkManager.class);
        Array<Chunk> chunks = chunkManager.getChunksSurroundingPlayer(game.world.getManager(ServerPlayerManager.class).getPlayerByConnection(connection));

        NetworkSendSystem networkSendSystem = game.world.getSystem(NetworkSendSystem.class);
        for (Chunk chunk : chunks) {
            game.network.sendObjectTCP(connection, new ChunkPacket(chunk));
            for (UUID eUUID : chunk.getEntities()) {
                networkSendSystem.sendEntity(connection, eUUID);
            }
        }
    }
}
