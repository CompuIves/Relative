package com.ives.relative.managers;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.CollisionEvent;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.StoppedMovementEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.managers.planet.chunks.ChunkManager;
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

    @Override
    protected void initialize() {
        super.initialize();
        world.getManager(EventManager.class).addObserver(this);
    }

    public boolean isEntityAuthorized(Entity e) {
        return mAuthority.has(e);
    }

    public boolean isEntityTemporaryAuthorized(Entity e) {
        if (isEntityAuthorized(e)) {
            return mAuthority.get(e).type == AuthorityType.PERMANENT;
        } else {
            return false;
        }
    }

    public boolean isEntityAuthorizedByPlayer(int connection, @NotNull Entity e) {
        if (mAuthority.has(e)) {
            Authority authority = mAuthority.get(e);
            return authority.getOwner() == connection;
        } else {
            return false;
        }
    }

    /**
     * Authorizes an entity to an owner
     *
     * @param owner
     * @param e
     * @param type
     */
    public void authorizeEntity(int owner, Entity e, AuthorityType type) {
        if (owner != -1) {
            Authority authority;
            if (!mAuthority.has(e)) {
                authority = e.edit().create(Authority.class);
                authority.type = type;
            } else {
                authority = mAuthority.get(e);
                if (authority.type == AuthorityType.PERMANENT)
                    return;
            }

            //If the object isn't being controlled at this moment by someone else
            if (!authority.onGoing) {
                //Mark it as controlled
                authority.onGoing = true;
                if (!authority.getOwners().contains(owner, true)) {
                    authority.setOwner(owner);
                    System.out.println("Added AuthorityType: " + type.toString() + " of entity: " + e.getId() + " to " + owner);
                    if (authority.type == AuthorityType.TOUCH) {
                        for (UUID eUUID : mPhysics.get(e).entitiesInContact) {
                            Entity e2 = uuidEntityManager.getEntity(eUUID);
                            if (mPhysics.get(e2).bodyType == BodyDef.BodyType.DynamicBody) {
                                //Authorize every entity which touches the entity.
                                authorizeEntity(owner, e2, AuthorityType.TOUCH);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes an owner from an entity. If the owner was the last owner the whole entity will lose authority.
     * @param e
     * @param owner
     */
    public void unAuthorizeEntity(Entity e, int owner) {
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

    /**
     * Removes every authority from an entity.
     * @param e
     */
    public void unAuthorizeEntity(Entity e) {
        if (mAuthority.get(e).type != AuthorityType.PERMANENT) {
            e.edit().remove(Authority.class);
            System.out.println("Unauthorized entity: " + e.getId());
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof CollisionEvent) {
            CollisionEvent collisionEvent = (CollisionEvent) event;
            if (collisionEvent.start) {
                if (mAuthority.has(collisionEvent.e1)) {
                    if (mPhysics.get(collisionEvent.e2).bodyType == BodyDef.BodyType.DynamicBody)
                        authorizeEntity(mAuthority.get(collisionEvent.e1).getOwner(), collisionEvent.e2, AuthorityType.TOUCH);
                }
                if (mAuthority.has(collisionEvent.e2)) {
                    if (mPhysics.get(collisionEvent.e1).bodyType == BodyDef.BodyType.DynamicBody)
                        authorizeEntity(mAuthority.get(collisionEvent.e2).getOwner(), collisionEvent.e1, AuthorityType.TOUCH);
                }
            }
        } else if (event instanceof StoppedMovementEvent) {
            if (mAuthority.has(event.entity)) {
                mAuthority.get(event.entity).onGoing = false;
            }
        }
    }

    public static enum AuthorityType {
        PERMANENT, TOUCH
    }
}
