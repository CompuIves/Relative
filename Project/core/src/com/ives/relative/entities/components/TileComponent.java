package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 3/12/2014.
 */
public class TileComponent extends Component {
    SolidTile tile;

    public TileComponent(SolidTile tile) {
        this.tile = tile;
    }
}
