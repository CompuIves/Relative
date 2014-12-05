package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.*;

/**
 * Created by Ives on 5/12/2014.
 */
public class PlayerFactory {

    /**
     * Creates a player
     * @param internalName
     * @param realName
     * @param world
     * @param position
     * @param z
     * @param isControllable IMPORTANT, is only used by the client. If you let the server set a controllable component
     *                       all entities will get one. Which means that every player can control eachother. Not very nice.
     * @return
     */
    public static Entity createPlayer(String internalName, String realName, World world, Vector2 position, int z, boolean isControllable) {
        Entity e = new Entity();
        e.add(new NameComponent(internalName, realName));
        e.add(new VisualComponent(new Texture("player.png"), 1, 1));
        e.add(new HealthComponent(100));
        e.add(new PositionComponent(position, z, world));
        e.add(new VelocityComponent());
        e.add(new MovementSpeedComponent(1));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);

        Body body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.9f;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        e.add(new BodyComponent(body));

        if(isControllable) {
            e.add(new InputComponent());
        }

        return e;
    }
}
