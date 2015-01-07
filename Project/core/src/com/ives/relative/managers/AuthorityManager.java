package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.server.ServerPlayerManager;
import com.ives.relative.systems.server.ServerNetworkSystem;

/**
 * Created by Ives on 6/1/2015.
 * <p></p>
 * This manager
 */
@Wire
public class AuthorityManager extends Manager implements EntityEventObserver {
    protected ServerPlayerManager serverPlayerManager;
    protected ServerNetworkSystem serverNetworkSystem;
    protected NetworkManager networkManager;
    protected UuidEntityManager uuidEntityManager;

    protected ComponentMapper<Authority> mAuthority;
    protected ComponentMapper<Physics> mPhysics;

    public AuthorityManager() {
    }

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    @Override
    public void onNotify(Entity e, EntityEvent event) {

    }

    public boolean isEntityAuthorized(Entity e) {
        return mAuthority.has(e);
    }

    public void authorizeEntity(int owner, Entity e, AuthorityType type) {
        Authority authority = e.edit().create(Authority.class);
        authority.owner = owner;
        authority.type = type;

        if (authority.type == AuthorityType.PERMANENT) {
            Physics p = mPhysics.get(e);

            CircleShape sensorShape = new CircleShape();
            sensorShape.setRadius(4);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = sensorShape;
            fixtureDef.isSensor = true;
            Fixture sensorFixture = p.body.createFixture(fixtureDef);
            sensorFixture.setUserData(Authority.class);

            sensorShape.dispose();
        }
    }

    public static enum AuthorityType {
        PERMANENT, PROXIMITY, CHUNK
    }
}
