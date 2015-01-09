package com.ives.relative.entities.components.body;

import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 12/12/2014.
 */
public class Velocity extends Component implements Networkable {
    public float vx, vy;
    public float vr;
    public boolean isMoving = false;

    public Velocity() {
    }

    public Velocity(float vx, float vy, float vr) {
        this.vx = vx;
        this.vy = vy;
        this.vr = vr;
    }
}
