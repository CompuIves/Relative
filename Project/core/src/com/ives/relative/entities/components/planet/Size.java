package com.ives.relative.entities.components.planet;

import com.artemis.Component;

/**
 * Created by Ives on 12/1/2015.
 */
public class Size extends Component {
    /**
     * planetSize in chunks
     */
    public int planetSize;

    public Size() {
    }

    public Size(int planetSize) {
        this.planetSize = planetSize;
    }
}
