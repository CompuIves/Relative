package com.ives.relative.universe;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Transform;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.systems.planet.GravitySystem;
import com.ives.relative.universe.chunks.builders.SquarePlanet;
import com.ives.relative.universe.planets.TileManager;

import java.util.HashMap;

/**
 * Created by Ives on 18/1/2015.
 */
@Wire
public class UniverseSystem extends EntityProcessingSystem implements EntityEventObserver {
    public static final float ITERATIONS = 1 / 60f;
    private final Array<UniverseBody> galaxies;
    private final HashMap<String, UniverseBody> universeBodiesByID;
    private final String seed;

    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Transform> mTransform;
    protected ComponentMapper<Physics> mPhysics;

    protected EventManager eventManager;
    protected TileManager tileManager;
    protected UuidEntityManager uuidEntityManager;
    protected GravitySystem gravitySystem;
    private float accumulator;

    public UniverseSystem(String seed) {
        super(Aspect.getAspectForAll(Position.class, Physics.class, Transform.class));
        this.seed = seed;
        galaxies = new Array<UniverseBody>();
        universeBodiesByID = new HashMap<String, UniverseBody>();
    }

    @Override
    protected void initialize() {
        createTemporaryGalaxy();
        eventManager.addObserver(this);
    }

    @Override
    protected void begin() {
        accumulator += world.getDelta();
        while (accumulator >= ITERATIONS) {
            accumulator -= ITERATIONS;
            gravitySystem.process();
            for (UniverseBody universeBody : galaxies) {
                universeBody.update();
            }
        }
    }

    @Override
    protected void process(Entity e) {
        Transform t = mTransform.get(e);
        Position p = mPosition.get(e);
        Physics physics = mPhysics.get(e);
        UniverseBody u = p.universeBody;

        if (physics.secondBody == null) {
            if (Math.abs(p.x) + t.width / 2 > Math.abs(u.width / 2)) {
                createSecondBody(e, p, true);
            } else if (Math.abs(p.y) + t.height / 2 > Math.abs(u.height / 2)) {
                createSecondBody(e, p, false);
            }
        } else {
            transferPosition(p, physics);

            if (Math.abs(p.x) - t.width / 2 > u.width / 2 || Math.abs(p.y) - t.height / 2 > u.height / 2) {
                if (p.universeBody.getTopUniverseBody(new Vector3(p.x, p.y, p.rotation), false).equals(p.universeBody)) {
                    physics.secondUniverseBody.world.destroyBody(physics.secondBody);
                    physics.secondUniverseBody = null;
                    physics.secondBody = null;
                } else {
                    Vector3 pos = new Vector3(p.x, p.y, p.rotation);
                    p.universeBody.transformVectorToUniverseBody(physics.secondUniverseBody, pos);
                    p.x = pos.x;
                    p.y = pos.y;
                    p.rotation = pos.z;

                    p.universeBody.world.destroyBody(physics.body);
                    p.universeBody = physics.secondUniverseBody;
                    physics.secondUniverseBody = null;
                    physics.body = physics.secondBody;
                    physics.secondBody = null;
                }
            }
        }
    }

    private void createSecondBody(Entity e, Position p, boolean x) {
        Physics physics = mPhysics.get(e);
        Vector3 transform = new Vector3(p.x, p.y, p.rotation);

        if (x) {
            if (transform.x > 0) {
                transform.x++;
            } else {
                transform.x--;
            }
        } else {
            if (transform.y > 0) {
                transform.y++;
            } else {
                transform.y--;
            }
        }

        UniverseBody ub = p.universeBody.getTopUniverseBody(transform, false);


        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(transform.x, transform.y);
        bodyDef.angle -= transform.z;

        Vector2 vel = new Vector2(physics.body.getLinearVelocity());
        vel.x += p.universeBody.vx;
        vel.y += p.universeBody.vy;
        bodyDef.linearVelocity.set(vel);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = physics.body.getLinearDamping();
        bodyDef.fixedRotation = false;

        physics.secondBody = ub.world.createBody(bodyDef);
        for (Fixture fixture : physics.body.getFixtureList()) {
            copyFixture(fixture, physics.secondBody);
        }
        physics.secondUniverseBody = ub;
    }

    private void copyFixture(Fixture fixture, Body body2) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = fixture.getShape();
        fixtureDef.isSensor = fixture.isSensor();
        fixtureDef.friction = fixture.getFriction();
        fixtureDef.density = fixture.getDensity();
        fixtureDef.restitution = fixture.getRestitution();
        body2.createFixture(fixtureDef);
    }

    private void transferPosition(Position p, Physics physics) {
        Vector3 pos = new Vector3(p.x, p.y, p.rotation);
        p.universeBody.transformVectorToUniverseBody(physics.secondUniverseBody, pos);

        Vector2 vel = physics.body.getLinearVelocity();
        physics.secondBody.setLinearVelocity(vel);
        physics.secondBody.setTransform(pos.x, pos.y, pos.z * MathUtils.degreesToRadians);
    }

    /**
     * Get an universebody by ID.
     *
     * @param id id of the universebody
     * @return return the universebody
     */
    public UniverseBody getUniverseBody(String id) {
        return universeBodiesByID.get(id);
    }

    public void createTemporaryGalaxy() {
        CollisionManager collisionManager = world.getManager(CollisionManager.class);

        //Create a simple galaxy before generating this is handled
        UniverseBody galaxy = new UniverseBody(collisionManager, "andromeda", null, 0, 0, 100000, 100000, 0, new Vector2(1, 1), 512);
        universeBodiesByID.put("andromeda", galaxy);
        galaxies.add(galaxy);

        UniverseBody starSystem = new UniverseBody(collisionManager, "starsystem101", null, 0, 0, 50000, 50000, 0, new Vector2(1, 1), 256);
        universeBodiesByID.put("starsystem101", starSystem);
        galaxy.addChild(starSystem);

        UniverseBody solarSystem = new UniverseBody(collisionManager, "ivesolaria", null, 0, 0, 10000, 10000, 0, new Vector2(1, 1), 32);
        universeBodiesByID.put("ivesolaria", solarSystem);
        starSystem.addChild(solarSystem);

        Planet.Builder planetBuilder = new Planet.Builder("ives", "ivesiscool", solarSystem, 300, 300, 140, 140, new Vector2(-10, 0), collisionManager);
        Planet earth = planetBuilder.build();
        universeBodiesByID.put("ives", earth);
        earth.setChunkBuilder(new SquarePlanet(earth, tileManager, uuidEntityManager, earth.gravity));
        solarSystem.addChild(earth);
    }

    @Override
    public void onNotify(EntityEvent event) {

    }
}
