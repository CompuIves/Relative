package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 2/12/2014.
 */
public class HealthComponent extends Component {
    int health = 100;

    public HealthComponent(int health) {
        this.health = health;
    }
}
