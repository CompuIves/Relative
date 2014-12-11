package com.ives.relative.entities.components;

import com.artemis.Component;
import com.ives.relative.entities.managers.SolidTile;

/**
 * Created by Ives on 3/12/2014.
 */
public class TileComponent extends Component {
    transient SolidTile tile;

    public TileComponent() {
    }

    public TileComponent(SolidTile tile) {
        this.tile = tile;
    }
}
