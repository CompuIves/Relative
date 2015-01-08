package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.ProximityAuthorityEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.planet.ChunkManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Ives on 6/1/2015.
 * <p></p>
 * This manager
 */
@Wire
public class AuthorityManager extends Manager implements EntityEventObserver {
    protected NetworkManager networkManager;
    protected ChunkManager chunkManager;

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
        sensorShape.setRadius(4);
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
            return authority.owner == connection;
        } else {
            Position position = mPosition.get(e);
            return chunkManager.getChunk(position.x, position.planet).owner == connection;
        }
    }

    public void authorizeEntity(int owner, Entity e, AuthorityType type) {
        Authority authority = e.edit().create(Authority.class);
        authority.owner = owner;
        authority.type = type;

        if (authority.type == AuthorityType.PERMANENT) {
            Physics p = mPhysics.get(e);
            addProximitySensor(p);
        }

        System.out.println("Authorized entity to " + owner);
    }

    public void unAuthorizeEntity(Entity e) {
        if (mAuthority.has(e)) {
            e.edit().remove(Authority.class);
        }
        System.out.println("Unauthorized entity");
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {
        if (event instanceof ProximityAuthorityEvent) {
            ProximityAuthorityEvent proximityEvent = (ProximityAuthorityEvent) event;
            if (!mAuthority.has(proximityEvent.object)) {
                if (proximityEvent.start) {
                    int owner = mAuthority.get(e).owner;
                    authorizeEntity(owner, proximityEvent.object, AuthorityType.PROXIMITY);
                }
            } else {
                if (!proximityEvent.start) {
                    unAuthorizeEntity(proximityEvent.object);
                }
            }
        }
    }

    public static enum AuthorityType {
        PERMANENT, PROXIMITY, CHUNK
    }
}
