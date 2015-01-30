package com.ives.relative.network.packets.handshake.planet;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.server.NetworkSendSystem;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;

/**
 * Created by Ives on 7/1/2015.
 * Sends the information of a chunk to the client.
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestChunk extends ResponsePacket {
    int x, y;
    String universeBody;

    public RequestChunk() {
    }

    public RequestChunk(String universeBody, int x, int y) {
        this.x = x;
        this.y = y;
        this.universeBody = universeBody;
    }

    @Override
    public void response(GameManager game) {
        UniverseBody u = game.world.getSystem(UniverseSystem.class).getUniverseBody(universeBody);
        Chunk chunk = game.world.getManager(ChunkManager.class).getChunk(u, new Vector2(x, y));
        NetworkSendSystem networkSendSystem = game.world.getSystem(NetworkSendSystem.class);

        ChunkPacket packet = networkSendSystem.generateFullChunkPacket(chunk);
        game.network.sendObjectTCP(connection, packet);
    }
}
