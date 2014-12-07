package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Ives on 2/12/2014.
 */
public class PositionComponent extends Component {
    public Vector2 position;
    public int z = 0;
    public String world;

    public PositionComponent(Vector2 position, int z, String world) {
        this.position = position;
        this.z = z;
        this.world = world;
    }

    public PositionComponent(float x, float y, int z, String world) {
        this.position = new Vector2(x, y);
        this.z = z;
        this.world = world;
    }
}
