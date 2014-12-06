package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.*;
import com.ives.relative.planet.tiles.TileManager;

/**
 * Created by Ives on 5/12/2014.
 */
public class PlayerFactory {

    /**
     * Creates a player
     * @param internalName local name
     * @param realName name visible to other players
     * @param world the world the player is in
     * @param position position
     * @param z depth
     * @return a new entity (player)
     */
    public static Entity createPlayer(String internalName, String realName, World world, Vector2 position, int z) {
        Entity e = new Entity();
        e.add(new NameComponent(internalName, realName));
        e.add(new VisualComponent(new Texture("player.png"), 1, 1));
        e.add(new HealthComponent(100));
        e.add(new PositionComponent(position, z, world));
        e.add(new MovementSpeedComponent(3.5f));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y);

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        Shape shape = TileManager.getCube(1.0f, 1.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 10.0f;
        fixtureDef.friction = 0.8f;
        Fixture fixture = body.createFixture(fixtureDef);
/*
        shape = new CircleShape();
        shape.setRadius(0.5f);
        fixtureDef.density = 1.0f;
        body.createFixture(fixtureDef);
*/
        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        e.add(new BodyComponent(body));

        return e;
    }
}
