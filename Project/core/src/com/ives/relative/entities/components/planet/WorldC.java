package com.ives.relative.entities.components.planet;


import com.artemis.Component;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Ives on 4/12/2014.
 */
public class WorldC extends Component {
    public transient World world = null;
    public int velocityIterations = 8;
    public int positionIterations = 8;
    public float acc = 0;

    public WorldC() {
    }

    public WorldC(World world, int velocityIterations, int positionIterations) {
        this.world = world;
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
    }
}
