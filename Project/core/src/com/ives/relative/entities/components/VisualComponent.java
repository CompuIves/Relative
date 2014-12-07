package com.ives.relative.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Ives on 3/12/2014.
 */
public class VisualComponent extends Component {
    public TextureRegion texture;
    public float width;
    public float height;

    public VisualComponent(TextureRegion texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
