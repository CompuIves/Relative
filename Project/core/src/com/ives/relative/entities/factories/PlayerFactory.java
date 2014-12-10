package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.HealthComponent;
import com.ives.relative.entities.components.MovementSpeedComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.body.BodyComponent;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class PlayerFactory extends Factory {

    /**
     * Creates a player
     * @param internalName local name
     * @param realName name visible to other players
     * @param planet the entity of the world the player is in
     * @param position position
     * @param z depth
     * @return a new entity (player)
     */
    public Entity createPlayer(String internalName, String realName, Entity planet, Vector2 position, int z) {
        String worldID = Mappers.name.get(planet).internalName;
        Entity e = new Entity();
        e.add(new NameComponent(internalName, realName));
        e.add(new VisualComponent(new TextureRegion(new Texture("player.png")), 1, 1));
        e.add(new HealthComponent(100));
        e.add(new MovementSpeedComponent(3.5f));
        Body body = createBody(e, position.x, position.y, 0, 0, planet);
        e.add(new BodyComponent(body, z, worldID));

        return e;
    }

    @Override
    public Body createBody(Entity e, float x, float y, float vx, float vy, Entity planet) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        World world = Mappers.world.get(planet).world;

        Body body = world.createBody(bodyDef);
        body.setFixedRotation(true);
        FixtureDef fixtureDef = new FixtureDef();
        Shape shape = new CircleShape();
        fixtureDef.shape = shape;
        shape.setRadius(0.5f);

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
        return body;
    }

    @Override
    public VisualComponent createVisual() {
        return (new VisualComponent(new TextureRegion(new Texture("player.png")), 1, 1));
    }
}
