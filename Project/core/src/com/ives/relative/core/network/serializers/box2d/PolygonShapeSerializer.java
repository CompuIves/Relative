package com.ives.relative.core.network.serializers.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 12/12/2014.
 */
public class PolygonShapeSerializer extends Serializer<PolygonShape> {
    public final static PolygonShapeSerializer instance = new PolygonShapeSerializer();
    private final static Vector2 tmp = new Vector2();

    @Override
    public void write(Kryo kryo, Output output, PolygonShape object) {
        int vertexCount = object.getVertexCount();
        output.writeByte(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            object.getVertex(i, tmp);
            output.writeFloat(tmp.x);
            output.writeFloat(tmp.y);
        }
    }

    @Override
    public PolygonShape read(Kryo kryo, Input input, Class<PolygonShape> type) {
        PolygonShape polygonShape = new PolygonShape();
        int vertexCount = input.readByte();
        float[] vertices = new float[vertexCount * 2];
        for (int i = 0; i < vertices.length; i++)
            vertices[i] = input.readFloat();
        polygonShape.set(vertices);
        return polygonShape;
    }
}
