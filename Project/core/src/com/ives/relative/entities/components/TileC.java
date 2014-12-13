package com.ives.relative.entities.components;

import com.artemis.Component;
import com.ives.relative.managers.SolidTile;

/**
 * Created by Ives on 3/12/2014.
 */
public class TileC extends Component {
    public String id;
    transient SolidTile tile;

    public TileC() {
    }

    public TileC(SolidTile tile) {
        this.tile = tile;
        this.id = tile.id;
    }
}
