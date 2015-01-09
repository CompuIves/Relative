package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.CollisionEvent;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.ProximityAuthorityEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.planet.ChunkManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Created by Ives on 6/1/2015.
 * <p></p>
 * This manager
 */
@Wire
public class AuthorityManager extends Manager implements EntityEventObserver {
    protected NetworkManager networkManager;
    protected ChunkManager chunkManager;
    protected UuidEntityManager uuidEntityManager;

    protected ComponentMapper<Authority> mAuthority;
    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<Position> mPosition;

    public AuthorityManager() {
    }

    public static void addProximitySensor(Physics p) {
        Body body = p.body;
        //Small check if the sensor is already added
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.getUserData().equals(Authority.class))
                return;
        }

        CircleShape sensorShape = new CircleShape();
        sensorShape.setRadius(2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true;
        Fixture sensorFixture = body.createFixture(fixtureDef);
        sensorFixture.setUserData(Authority.class);

        sensorShape.dispose();
    }

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    public boolean isEntityAuthorized(Entity e) {
        return mAuthority.has(e);
    }

    public boolean isEntityAuthorizedByPlayer(int connection, @NotNull Entity e) {
        if (mAuthority.has(e)) {
            Authority authority = mAuthority.get(e);
            return authority.getOwner() == connection;
        } else {
            Position position = mPosition.get(e);
            return chunkManager.getChunk(position.x, position.planet).owner == connection;
        }
    }

    public void authorizeEntity(int owner, Entity e, AuthorityType type) {
        if (owner != -1) {
            Authority authority;
            if (!mAuthority.has(e)) {
                authority = e.edit().create(Authority.class);
                authority.type = type;
            } else {
                authority = mAuthority.get(e);
            }

            if (type == AuthorityType.PERMANENT) {
                Physics p = mPhysics.get(e);
                addProximitySensor(p);
            }

            if (!authority.getOwners().contains(owner, true)) {
                authority.setOwner(owner);
                if (type == AuthorityType.PROXIMITY) {
                    for (UUID eUUID : mPhysics.get(e).entitiesInContact) {
                        Entity e2 = uuidEntityManager.getEntity(eUUID);
                        if (mPhysics.get(e2).bodyType == BodyDef.BodyType.DynamicBody) {
                            authorizeEntity(owner, e2, type);
                        }
                    }
                }

                System.out.println("Added AuthorityType: " + type.toString() + " to " + owner);
            }
        }
    }

    public void unAuthorizeEntity(Entity e, int owner) {
        if (mAuthority.has(e)) {
            Authority authority = mAuthority.get(e);
            if (authority.type != AuthorityType.PERMANENT) {
                System.out.println("Unauthorized entity");

                if (authority.getOwners().size == 1) {
                    e.edit().remove(Authority.class);
                } else {
                    //The array is sorted automatically, if the first owner is removed the second owner will shift up.
                    authority.removeOwner(owner);
                }
            }
        }
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {
        if (event instanceof ProximityAuthorityEvent) {
            ProximityAuthorityEvent proximityEvent = (ProximityAuthorityEvent) event;
            int owner = mAuthority.get(e).getOwner();
            if (proximityEvent.start) {
                authorizeEntity(owner, proximityEvent.object, AuthorityType.PROXIMITY);
            } else {
                unAuthorizeEntity(proximityEvent.object, owner);
            }
        } else if (event instanceof CollisionEvent) {
            CollisionEvent collisionEvent = (CollisionEvent) event;
            if (!collisionEvent.start) {
                if (mAuthority.has(collisionEvent.e1)) {

                }
            }
        }
    }

    public static enum AuthorityType {
        PERMANENT, PROXIMITY, CHUNK
    }
}
