package com.ives.relative.entities.components.living;


import com.artemis.Component;

/**
 * Created by Ives on 2/12/2014.
 * Defines the health of an entity.
 */
public class Health extends Component {
    public int health;

    public Health() {
    }

    public Health(int health) {
        this.health = health;
    }
}
