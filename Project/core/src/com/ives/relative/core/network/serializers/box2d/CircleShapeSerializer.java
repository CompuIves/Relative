package com.ives.relative.core.network.serializers.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 12/12/2014.
 */
public class CircleShapeSerializer extends Serializer<CircleShape> {

    public static final CircleShapeSerializer instance = new CircleShapeSerializer();

    @Override
    public void write(Kryo kryo, Output output, CircleShape object) {
        output.writeFloat(object.getPosition().x);
        output.writeFloat(object.getPosition().y);
        output.writeFloat(object.getRadius());
    }

    @Override
    public CircleShape read(Kryo kryo, Input input, Class<CircleShape> type) {
        CircleShape shape = new CircleShape();
        shape.setPosition(new Vector2(input.readFloat(), input.readFloat()));
        shape.setRadius(input.readFloat());
        return shape;
    }
}
