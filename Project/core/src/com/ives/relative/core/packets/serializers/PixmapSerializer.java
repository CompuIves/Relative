package com.ives.relative.core.packets.serializers;

import com.badlogic.gdx.graphics.Pixmap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 7/12/2014.
 */
public class PixmapSerializer extends Serializer {
    @Override
    public void write(Kryo kryo, Output output, Object o) {
        if(o instanceof Pixmap) {
            Pixmap pixmap = (Pixmap) o;
            output.writeBytes(pixmap.getPixels().array());
        }
    }

    @Override
    public Object read(Kryo kryo, Input input, Class aClass) {
        byte[] bytes;
        return null;
    }
}
