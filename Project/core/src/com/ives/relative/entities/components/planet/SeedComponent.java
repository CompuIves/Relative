package com.ives.relative.entities.components.planet;

import com.badlogic.ashley.core.Component;

/**
 * Created by Ives on 10/12/2014.
 */
public class SeedComponent extends Component {
    public String seed;

    public SeedComponent() {
    }

    public SeedComponent(String seed) {
        this.seed = seed;
    }
}
