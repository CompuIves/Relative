package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.PlayerManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.Health;
import com.ives.relative.entities.components.MovementSpeed;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Transform;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.factories.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 11/12/2014.
 */
@Wire
public class ServerPlayerManager extends PlayerManager {
    protected ComponentMapper<WorldC> mWorldComponent;
    //For the server
    private Map<Integer, Entity> playersByConnection;
    private Player player;

    public ServerPlayerManager() {
        super();
        playersByConnection = new HashMap<Integer, Entity>();
    }

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
    public Entity createPlayer(int connection, String internalName, String realName, Entity planet, Vector2 position, int z) {
        String worldID = world.getManager(PlanetManager.class).getPlanetID(planet);
        Entity e = new EntityBuilder(world).with(new Health(100),
                new MovementSpeed(3.5f),
                new Name(internalName, realName),
                new Visual(new TextureRegion(new Texture("player.png")), 1, 1),
                new Position(position.x, position.y, z, worldID),
                new Velocity(0, 0)).
                group("player").
                build();

        Body body = createBody(e, position.x, position.y, 0, 0, planet);
        e.edit().add(new Physics(body)).add(new Transform(1, 1, body.getFixtureList()));

        setPlayer(e, internalName);
        addConnection(connection, e);
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

    /**
     * Save a player by connection, used only by Server
     *
     * @param connection
     * @param player
     */
    public void addConnection(int connection, Entity player) {
        this.playersByConnection.put(connection, player);
    }

    /**
     * Get a player by connection, used only by Server
     *
     * @param connection
     * @return
     */
    public Entity getPlayerByConnection(int connection) {
        return this.playersByConnection.get(connection);
    }
}
