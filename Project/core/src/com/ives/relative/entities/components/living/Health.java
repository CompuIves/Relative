package com.ives.relative.entities.components.living;


import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 2/12/2014.
 */
public class Health extends Component implements Networkable {
    public int health;

    public Health(int health) {
        this.health = health;
    }

    public Health() {
    }
}
