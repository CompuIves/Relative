package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ives.relative.entities.components.client.Visual;

import java.nio.ByteBuffer;

/**
 * Created by Ives on 8/12/2014.
 */
public class NetworkVisualComponent extends Component {
    public byte[] textureBytes;
    public int iWidth, iHeight;
    public float eWidth, eHeight;
    public int format;

    String file;

    public NetworkVisualComponent() {
    }

    /**
     * Converts VisualComponent to a NetworkComponent
     * @param visual The VisualComponent which needs to be converted
     */
    public NetworkVisualComponent(Visual visual) {
        visual.texture.getTexture().getTextureData().prepare();
        Pixmap pixmap = visual.texture.getTexture().getTextureData().consumePixmap();
        ByteBuffer pixels = pixmap.getPixels();
        textureBytes = new byte[pixels.limit()];
        pixels.get(textureBytes, 0, textureBytes.length);

        format = pixmap.getFormat().ordinal();
        iWidth = pixmap.getWidth();
        iHeight = pixmap.getHeight();
        eWidth = visual.width;
        eHeight = visual.height;
    }

    /**
     * Creates a component without any information except the file name of the visual, when this will be converted back
     * it will just load the file from the system.
     * @param file File name of the visual
     * @param eWidth the width of the entity carrying this visual
     * @param eHeight the height of the entity carrying this visual.
     */
    public NetworkVisualComponent(String file, float eWidth, float eHeight) {
        this.file = file;
        this.eWidth = eWidth;
        this.eHeight = eHeight;
    }

    /**
     * Converts this component to a VisualComponent (if file isn't null it will just load the file from the local systen
     * instead of using the bytes)
     * @return the VisualComponent created
     */
    public Visual getComponent() {
        if(file == null || file.equals("")) {
            Pixmap pixmap = new Pixmap(iWidth, iHeight, Pixmap.Format.values()[format]);
            ByteBuffer pixels = pixmap.getPixels();
            pixels.clear();
            pixels.put(textureBytes);
            pixels.position(0);
            Texture texture = new Texture(pixmap);
            TextureRegion textureRegion = new TextureRegion(texture);
            return new Visual(textureRegion, eWidth, eHeight);
        } else {
            return new Visual(new TextureRegion(new Texture(Gdx.files.local(file))), eWidth, eHeight);
        }
    }
}
