package com.ives.relative.core.packets.handshake;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.updates.WorldSnapshotPacket;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.ServerPlayerManager;

/**
 * Created by Ives on 13/12/2014.
 * <p/>
 * HANDLED BY SERVER
 */
public class RequestWorldSnapshot extends Packet {

    public RequestWorldSnapshot() {
    }

    @Override
    public void response(GameManager game) {
        Connection connection1 = ServerNetwork.getConnection(connection);
        Array<Entity> players = game.world.getManager(ServerPlayerManager.class).getPlayers();
        Array<Long> ids = new Array<Long>();
        for (Entity player : players) {
            ids.add(game.world.getManager(NetworkManager.class).getNetworkID(player));
        }
        connection1.sendTCP(new WorldSnapshotPacket(players, ids));
    }
}
