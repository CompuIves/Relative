package com.ives.relative.core.network.serializers.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 12/12/2014.
 */
public class EdgeShapeSerializer extends Serializer<EdgeShape> {
    public static final EdgeShapeSerializer instance = new EdgeShapeSerializer();
    private static final byte NONE = 0, V0 = 1, V3 = 2, BOTH = 3;
    static Vector2 tmp = new Vector2();

    @Override
    public void write(Kryo kryo, Output output, EdgeShape object) {
        object.getVertex1(tmp);
        output.writeFloat(tmp.x);
        output.writeFloat(tmp.y);
        object.getVertex2(tmp);
        output.writeFloat(tmp.x);
        output.writeFloat(tmp.y);

        if (object.hasVertex0()) {
            if (object.hasVertex3()) {
                output.writeByte(BOTH);
            } else {
                output.writeByte(V0);
            }
        } else if (object.hasVertex3()) {
            output.writeByte(V3);
        } else {
            output.writeByte(NONE);
        }

        if (object.hasVertex0()) {
            object.getVertex0(tmp);
            output.writeFloat(tmp.x);
            output.writeFloat(tmp.y);
        }
        if (object.hasVertex3()) {
            object.getVertex3(tmp);
            output.writeFloat(tmp.x);
            output.writeFloat(tmp.y);
        }
    }

    @Override
    public EdgeShape read(Kryo kryo, Input input, Class<EdgeShape> type) {
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
        byte next = input.readByte();
        if (next == BOTH) {
            edgeShape.setVertex0(input.readFloat(), input.readFloat());
            edgeShape.setVertex3(input.readFloat(), input.readFloat());
        } else if (next == V0) {
            edgeShape.setVertex0(input.readFloat(), input.readFloat());
        } else if (next == V3) {
            edgeShape.setVertex0(input.readFloat(), input.readFloat());
        }

        return edgeShape;
    }
}
