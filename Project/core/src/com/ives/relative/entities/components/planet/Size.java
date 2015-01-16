package com.ives.relative.entities.components.planet;

import com.artemis.Component;

/**
 * Created by Ives on 12/1/2015.
 */
public class Size extends Component {
    /**
     * Width in chunks
     */
    public int width;
    /**
     * Height in chunks
     */
    public int height;

    public Size() {
    }

    public Size(int width, int height) {
        this.height = height;
        this.width = width;
    }
}
