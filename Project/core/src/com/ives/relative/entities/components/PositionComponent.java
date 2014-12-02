package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.ives.relative.world.Planet;

/**
 * Created by Ives on 2/12/2014.
 */
public class PositionComponent extends Component {
    public float x = 0.0f;
    public float y = 0.0f;
    public float z = 0.0f;
    public Planet planet = null;
}
