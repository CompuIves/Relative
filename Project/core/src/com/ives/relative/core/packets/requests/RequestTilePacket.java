package com.ives.relative.core.packets.requests;

import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.TilePacket;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.security.KeyStore;
import java.util.Map;

/**
 * Created by Ives on 8/12/2014.
 */
public class RequestTilePacket implements Packet {
    int connection;

    public RequestTilePacket() {
    }

    public RequestTilePacket(int connection) {
        this.connection = connection;
    }

    @Override
    public void handle(GameManager game) {
        for(Map.Entry entry : game.tileManager.solidTiles.entrySet()) {
            SolidTile tile = (SolidTile) entry.getValue();
            game.proxy.network.sendObjectTCP(connection, new TilePacket(tile));
        }
    }
}
