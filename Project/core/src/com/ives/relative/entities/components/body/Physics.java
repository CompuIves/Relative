package com.ives.relative.entities.components.body;

import com.artemis.Entity;
import com.artemis.World;
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
 */
public class Physics extends CustomNetworkComponent {
    public transient Body body = null;
    public transient Array<Contact> contacts;
    public transient Array<UUID> entitiesInContact;
    public BodyDef.BodyType bodyType;

    public Physics() {
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();
    }

    public Physics(Body body, BodyDef.BodyType bodyType) {
        this.body = body;
        this.bodyType = bodyType;
        contacts = new Array<Contact>();
        entitiesInContact = new Array<UUID>();

        dependants.add(Position.class.getSimpleName());
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {

    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        Position position = world.getMapper(Position.class).get(e);
        switch (type) {
            case TILE:
                TileC tileC = world.getMapper(TileC.class).get(e);
                body = TileFactory.createBody(position.universeBody, e, tileC.tile, position.x, position.y, true);
                break;
            case PLAYER:
                Velocity velocity = world.getMapper(Velocity.class).get(e);
                body = PlayerFactory.createBody(position.universeBody, e, position.x, position.y, velocity.vx, velocity.vy);
                break;
        }
    }
}
