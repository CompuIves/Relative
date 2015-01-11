package com.ives.relative.entities.components.planet;

import com.artemis.Component;
import com.ives.relative.entities.components.network.Networkable;

/**
 * Created by Ives on 10/12/2014.
 * A random seed for the world, gets used by generation techniques.
 */
public class Seed extends Component implements Networkable {
    public String seed;

    public Seed() {
    }

    public Seed(String seed) {
        this.seed = seed;
    }
}
