package com.ives.relative.entities.components.tile;


import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 5/12/2014.
 */
public class Durability extends Component implements Networkable {
    public int durability = 10;

    public Durability(int durability) {
        this.durability = durability;
    }

    public Durability() {
    }
}
