package com.ives.relative.entities.components.tile;

import com.artemis.Entity;
import com.artemis.World;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.universe.planets.TileManager;

/**
 * Created by Ives on 3/12/2014.
 */
public class TileC extends CustomNetworkComponent {
    public String id;
    public transient SolidTile tile;

    public TileC() {
    }

    public TileC(SolidTile tile) {
        this.tile = tile;
        this.id = tile.id;
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {

    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        tile = world.getManager(TileManager.class).solidTiles.get(id);
    }
}
