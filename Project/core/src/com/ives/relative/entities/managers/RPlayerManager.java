package com.ives.relative.entities.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.PlayerManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.body.PhysicsPosition;
import com.ives.relative.entities.components.planet.WorldComponent;
import com.ives.relative.entities.factories.Player;

/**
 * Created by Ives on 11/12/2014.
 */
@Wire
public class RPlayerManager extends PlayerManager {
    protected ComponentMapper<WorldComponent> mWorldComponent;
    private Player player;

    /**
     * Creates a player
     *
     * @param internalName local name
     * @param realName     name visible to other players
     * @param planet       the entity of the world the player is in
     * @param position     position
     * @param z            depth
     * @return a new entity (player)
     */
    public Entity createPlayer(String internalName, String realName, Entity planet, Vector2 position, int z) {
        String worldID = world.getManager(PlanetManager.class).getPlanetID(planet);
        Entity e = player.health(100).mvSpeed(3.5f).name(internalName, realName).create();
        e.edit().add(new VisualComponent(new TextureRegion(new Texture("player.png")), 1, 1));
        Body body = createBody(e, position.x, position.y, 0, 0, planet);
        e.edit().add(new PhysicsPosition(body, z, worldID));
        setPlayer(e, internalName);
        return e;
    }

    public Body createBody(Entity e, float x, float y, float vx, float vy, Entity planet) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        World world = mWorldComponent.get(planet).world;

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
}
