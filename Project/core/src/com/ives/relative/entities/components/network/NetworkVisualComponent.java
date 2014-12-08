package com.ives.relative.entities.components.network;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ives.relative.entities.components.VisualComponent;

import java.nio.ByteBuffer;

/**
 * Created by Ives on 8/12/2014.
 */
public class NetworkVisualComponent extends Component{
    public byte[] textureBytes;
    public int iWidth, iHeight;
    public float eWidth, eHeight;
    public int format;

    public NetworkVisualComponent() {
    }

    public NetworkVisualComponent(VisualComponent visualComponent) {
        visualComponent.texture.getTexture().getTextureData().prepare();
        Pixmap pixmap = visualComponent.texture.getTexture().getTextureData().consumePixmap();
        ByteBuffer pixels = pixmap.getPixels();
        textureBytes = new byte[pixels.limit()];
        pixels.get(textureBytes, 0, textureBytes.length);

        format = pixmap.getFormat().ordinal();
        iWidth = pixmap.getWidth();
        iHeight = pixmap.getHeight();
        eWidth = visualComponent.width;
        eHeight = visualComponent.height;
    }

    public VisualComponent getComponent() {
        Pixmap pixmap = new Pixmap(iWidth, iHeight, Pixmap.Format.values()[format]);
        ByteBuffer pixels = pixmap.getPixels();
        pixels.clear();
        pixels.put(textureBytes);
        pixels.position(0);
        Texture texture = new Texture(pixmap);
        TextureRegion textureRegion = new TextureRegion(texture);
        return new VisualComponent(textureRegion, eWidth, eHeight);
    }
}
