package com.ives.relative.entities.components.tile;

import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;
import com.ives.relative.managers.SolidTile;

/**
 * Created by Ives on 3/12/2014.
 */
public class TileC extends Component implements Networkable {
    public String id;
    transient SolidTile tile;

    public TileC() {
    }

    public TileC(SolidTile tile) {
        this.tile = tile;
        this.id = tile.id;
    }
}
