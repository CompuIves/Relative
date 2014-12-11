package com.ives.relative.entities.components;


import com.artemis.Component;

/**
 * Created by Ives on 5/12/2014.
 */
public class DurabilityComponent extends Component {
    public int durability = 10;

    public DurabilityComponent(int durability) {
        this.durability = durability;
    }

    public DurabilityComponent() {
    }
}
