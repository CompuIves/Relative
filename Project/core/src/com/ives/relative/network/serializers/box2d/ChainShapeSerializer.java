package com.ives.relative.network.serializers.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 12/12/2014.
 */
public class ChainShapeSerializer extends Serializer<ChainShape> {
    public static final ChainShapeSerializer instance = new ChainShapeSerializer();

    private static final Vector2 tmp = new Vector2();

    @Override
    public void write(Kryo kryo, Output output, ChainShape object) {
        output.writeBoolean(object.isLooped());
        int vertexCount = object.getVertexCount();
        output.writeByte(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            object.getVertex(i, tmp);
            output.writeFloat(tmp.x);
            output.writeFloat(tmp.y);
        }
    }

    @Override
    public ChainShape read(Kryo kryo, Input input, Class<ChainShape> type) {
        ChainShape shape = new ChainShape();
        boolean isLooped = input.readBoolean();
        float[] vertices = new float[input.readByte() * 2];

        for (int i = 0; i < vertices.length; i++)
            vertices[i] = input.readFloat();


        if (isLooped) {
            shape.createLoop(vertices);
        } else {
            shape.createChain(vertices);
        }

        return shape;
    }
}
