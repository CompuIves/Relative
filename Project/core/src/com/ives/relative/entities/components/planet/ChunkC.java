package com.ives.relative.entities.components.planet;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.planet.chunks.PlanetSide;

/**
 * Created by Ives on 4/1/2015.
 */
public class ChunkC extends Component {
    public transient Array<PlanetSide> sides;

    public ChunkC() {
        sides = new Array<PlanetSide>();
    }
}
