package com.ives.relative.universe;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
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
import com.ives.relative.entities.events.position.UniverseBodyCollisionEvent;
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
        Physics physics = mPhysics.get(e);
        if (physics.secondBody != null) {
            transformSecondBody(e, physics);
        }
    }

    private void createSecondBody(Entity e, UniverseBody ub) {
        Physics physics = mPhysics.get(e);
        if (physics.secondBody == null && physics.body.getType() == BodyDef.BodyType.DynamicBody) {
            Gdx.app.log("UniverseSystem", "Creating second body for " + e + " in " + ub);

            Position p = mPosition.get(e);
            BodyDef bodyDef = new BodyDef();

            //Transform position + velocity to new universebody
            Vector3 transform = new Vector3(p.x, p.y, p.rotation);
            p.universeBody.transformVectorToUniverseBody(ub, transform);
            bodyDef.position.set(transform.x, transform.y);
            bodyDef.angle -= transform.z;
            Vector2 vel = physics.body.getLinearVelocity();
            transform.set(vel, 0);
            p.universeBody.transformVectorToUniverseBody(ub, transform);
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
    }

    private void removeSecondaryBody(Entity e) {
        Physics physics = mPhysics.get(e);
        if (physics.secondBody != null) {
            Position p = mPosition.get(e);
            UniverseBody newUniverseBody = p.universeBody.getTopUniverseBody(new Vector2(p.x, p.y), true);
            Gdx.app.log("UniverseSystem", "Removing " + e + " from UniverseBody");
            if (p.universeBody.equals(newUniverseBody)) {
                //This means the player is back in its previous body
                physics.secondUniverseBody.removeBody(physics.secondBody);
                physics.secondUniverseBody = null;
                physics.secondBody = null;
            } else {
                Vector3 pos = new Vector3(p.x, p.y, p.rotation);
                p.universeBody.transformVectorToUniverseBody(newUniverseBody, pos);
                p.x = pos.x;
                p.y = pos.y;
                p.rotation = pos.z;

                p.universeBody.removeBody(physics.body);
                p.universeBody = newUniverseBody;
                physics.secondUniverseBody = null;
                physics.body = physics.secondBody;
                physics.secondBody = null;
            }
        }
    }

    private void transformSecondBody(Entity e, Physics physics) {
        Position p = mPosition.get(e);
        Vector3 pos = new Vector3(p.x, p.y, p.rotation);
        p.universeBody.transformVectorToUniverseBody(physics.secondUniverseBody, pos);

        Vector2 vel = physics.body.getLinearVelocity();
        p.universeBody.transformVectorToUniverseBody(physics.secondUniverseBody, vel);
        physics.secondBody.setLinearVelocity(vel);
        physics.secondBody.setTransform(pos.x, pos.y, pos.z * MathUtils.degreesToRadians);
        physics.secondBody.setUserData(e);
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

    /**
     * Get a universebody by ID.
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

        UniverseBody starSystem = new UniverseBody(collisionManager, "starsystem101", galaxy, 0, 0, 50000, 50000, 0, new Vector2(1, 1), 256);
        universeBodiesByID.put("starsystem101", starSystem);
        galaxy.addChild(starSystem);

        UniverseBody solarSystem = new UniverseBody(collisionManager, "ivesolaria", starSystem, 0, 0, 10000, 10000, 0, new Vector2(1, 1), 32);
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
        if (event instanceof UniverseBodyCollisionEvent) {
            UniverseBodyCollisionEvent universeBodyCollisionEvent = (UniverseBodyCollisionEvent) event;
            if (universeBodyCollisionEvent.start) {
                createSecondBody(event.entity, universeBodyCollisionEvent.universeBody);
            } else {
                removeSecondaryBody(event.entity);
            }
        }
    }
}
