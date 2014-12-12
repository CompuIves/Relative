package com.ives.relative.entities.components.client;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Ives on 3/12/2014.
 */
public class VisualComponent extends Component {
    public transient TextureRegion texture;
    public float width;
    public float height;

    public VisualComponent() {
    }

    public VisualComponent(TextureRegion texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
