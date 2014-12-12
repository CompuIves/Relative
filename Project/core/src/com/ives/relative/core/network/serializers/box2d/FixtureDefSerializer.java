package com.ives.relative.core.network.serializers.box2d;

import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by Ives on 12/12/2014.
 */
public class FixtureDefSerializer extends Serializer<FixtureDef> {

    @Override
    public void write(Kryo kryo, Output output, FixtureDef fixtureDef) {
        output.writeFloat(fixtureDef.density);
        output.writeFloat(fixtureDef.friction);
        output.writeFloat(fixtureDef.restitution);
        output.writeBoolean(fixtureDef.isSensor);
        output.writeShort(fixtureDef.filter.categoryBits);
        output.writeShort(fixtureDef.filter.maskBits);
        output.writeShort(fixtureDef.filter.groupIndex);
        kryo.writeObject(output, fixtureDef.shape.getType());
        switch (fixtureDef.shape.getType()) {
            case Circle:
                kryo.writeObject(output, fixtureDef.shape, CircleShapeSerializer.instance);
                break;
            case Edge:
                kryo.writeObject(output, fixtureDef.shape, EdgeShapeSerializer.instance);
                break;
            case Chain:
                kryo.writeObject(output, fixtureDef.shape, ChainShapeSerializer.instance);
                break;
            case Polygon:
                kryo.writeObject(output, fixtureDef.shape, PolygonShapeSerializer.instance);
        }
    }

    @Override
    public FixtureDef read(Kryo kryo, Input input, Class<FixtureDef> type) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = input.readFloat();
        fixtureDef.friction = input.readFloat();
        fixtureDef.restitution = input.readFloat();
        fixtureDef.isSensor = input.readBoolean();
        fixtureDef.filter.categoryBits = input.readShort();
        fixtureDef.filter.maskBits = input.readShort();
        fixtureDef.filter.groupIndex = input.readShort();
        Shape.Type shapeType = kryo.readObject(input, Shape.Type.class);
        switch (shapeType) {
            case Circle:
                fixtureDef.shape = kryo.readObject(input, CircleShape.class, CircleShapeSerializer.instance);
                break;
            case Edge:
                fixtureDef.shape = kryo.readObject(input, EdgeShape.class, EdgeShapeSerializer.instance);
                break;
            case Chain:
                fixtureDef.shape = kryo.readObject(input, ChainShape.class, ChainShapeSerializer.instance);
                break;
            case Polygon:
                fixtureDef.shape = kryo.readObject(input, PolygonShape.class, PolygonShapeSerializer.instance);
        }
        return fixtureDef;
    }
}
