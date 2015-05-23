package com.ives.relative.entities.components.body;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.PlayerFactory;
import com.ives.relative.factories.TileFactory;
import com.ives.relative.managers.NetworkManager;

import java.util.UUID;

/**
 * Created by Ives on 2/12/2014.
 *
 * This component contains everything about the Box2D physics, this means that it has the bodies, contacts, entities in contacts
 * etc.
 * There is a secondBody, sometimes an entity needs 2 bodies to be in 2 UniverseBodies.
 */
public class Physics extends CustomNetworkComponent {
    public transient Body body = null;
    public transient Array<Contact> contacts;
    public transient Array<UUID> entitiesInContact;
    public BodyDef.BodyType bodyType;

    public float x, y;
    public float vx, vy;

    public Physics() {
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();
    }

    public Physics(Body body, BodyDef.BodyType bodyType) {
        this.body = body;
        this.bodyType = bodyType;
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();

        dependants.add(Location.class.getSimpleName());
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {
        if(type == NetworkManager.Type.TILE) {
            dependants.add(TileC.class.getSimpleName());
        }

        Vector2 p = body.getPosition();
        Vector2 v = body.getLinearVelocity();
        x = p.x;
        y = p.y;
        vx = v.x;
        vy = v.y;
    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        Location location = world.getMapper(Location.class).get(e);
        switch (type) {
            case TILE:
                TileC tileC = world.getMapper(TileC.class).get(e);
                body = TileFactory.createBody(location.space, e, tileC.tile, 15, x, y, true);
                break;
            case PLAYER:
                body = PlayerFactory.createBody(location.space, e, x, y, vx, vy);
                break;
        }
    }
}
