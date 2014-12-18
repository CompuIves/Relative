package com.ives.relative.managers;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.ComponentUtils;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.Gravity;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.Player;
import com.ives.relative.factories.Tile;
import com.ives.relative.network.packets.updates.ComponentPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 13/12/2014.
 */
@Wire
public class NetworkManager extends Manager {
    protected ComponentMapper<WorldC> mWorldC;
    protected ComponentMapper<Gravity> mGravity;
    protected ComponentMapper<NetworkC> mNetworkC;
    Map<Integer, Entity> networkEntities;
    Map<Entity, Integer> networkIDs;
    int freeID;
    Array<Integer> removedIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Integer, Entity>();
        networkIDs = new HashMap<Entity, Integer>();

        removedIDs = new Array<Integer>();
    }

    /**
     * Adds this entity with new id
     *
     * @param e entity to be added
     * @return the id it refers to
     */
    public int addEntity(Entity e) {
        int id = getFreeID();
        addEntity(id, e);
        return id;
    }

    public void addEntity(int id, Entity e) {
        if (networkEntities.containsKey(id) || networkIDs.containsKey(e)) {
            updateEntity(e, ComponentUtils.getComponents(e), false);
        } else {
            networkEntities.put(id, e);
            networkIDs.put(e, id);
        }
    }


    /**
     * When a remote entity is added there is a chance of duplicates, this method looks for the id and edits the
     * existing entity accordingly.
     *
     * @param id         id of the entity which needs to be changed
     * @param components the components of the old Entity
     * @param delta      should every component be removed before adding these components?
     * @param type       which type needs to be used for finishing the entity
     */
    public Entity addEntity(int id, Array<Component> components, boolean delta, Type type) {
        Entity e = addEntity(id, components, delta);
        finishEntity(e, type);
        return e;
    }

    /**
     * When a remote entity is added there is a chance of duplicates, this method looks for the id and edits the
     * existing entity accordingly.
     *
     * @param id id of the entity which needs to be changed
     * @param components the components of the old Entity
     * @param delta should every component be removed before adding these components?
     */
    public Entity addEntity(int id, Array<Component> components, boolean delta) {
        Entity e;
        if (networkEntities.containsKey(id)) {
            e = networkEntities.get(id);
            updateEntity(e, components, delta);
        } else {
            e = world.createEntity();
            addEntity(id, e);
        }
        return e;
    }


    public Entity updateEntity(Entity e, Array<Component> components, boolean delta) {
        if (!delta) {
            ComponentUtils.removeAllComponents(e);
        }
        ComponentUtils.addComponents(e, components);
        return e;
    }


    private void finishEntity(Entity entity, Type type) {
        Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
        Position position = entity.getWorld().getMapper(Position.class).get(entity);
        Visual visual = entity.getWorld().getMapper(Visual.class).get(entity);
        switch (type) {
            case PLAYER:
                physics = entity.getWorld().getMapper(Physics.class).get(entity);
                position = entity.getWorld().getMapper(Position.class).get(entity);
                Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
                Entity planet = entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID);

                physics.body = Player.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, planet);
                visual.texture = new TextureRegion(new Texture("player.png"));
                break;
            case TILE:
                TileC tileC = entity.getWorld().getMapper(TileC.class).get(entity);
                SolidTile tile = entity.getWorld().getManager(TileManager.class).solidTiles.get(tileC.id);
                com.badlogic.gdx.physics.box2d.World world = entity.getWorld().getMapper(WorldC.class).get(entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID)).world;
                physics.body = Tile.createBody(entity, tile, position.x, position.y, true, world);
                visual.texture = tile.textureRegion;
                break;
            case PLANET:
                Name name = entity.getWorld().getMapper(Name.class).get(entity);
                PlanetManager planetManager = entity.getWorld().getManager(PlanetManager.class);
                planetManager.addPlanet(name.internalName, entity);
                planetManager.generateTerrain(entity);
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }


    public void removeEntity(int id) {
        Entity e = networkEntities.get(id);
        ComponentUtils.removeAllComponents(e);
        removedIDs.add(id);

        networkEntities.remove(id);
        networkIDs.remove(e);

        System.out.println("Removed entity: " + e.getId());
        e.deleteFromWorld();
    }

    public Entity getEntity(int id) {
        if (networkEntities.containsKey(id)) {
            return networkEntities.get(id);
        } else {
            return null;
        }
    }


    public ComponentPacket generateFullComponentPacket(Entity e) {
        Type type = mNetworkC.get(e).type;
        return generateFullComponentPacket(e, type);
    }


    public ComponentPacket generateFullComponentPacket(Entity e, Type type) {
        Array<Component> components = ComponentUtils.getComponents(e);
        int id = getNetworkID(e);
        return new ComponentPacket(components, id, false, type);
    }

    public int getNetworkID(Entity e) {
        return networkIDs.get(e);
    }


    public int getFreeID() {
        if (removedIDs.size == 0) {
            freeID++;
            return freeID;
        } else {
            return removedIDs.first();
        }
    }

    public enum Type {
        PLAYER,
        TILE,
        PLANET, type, OTHER
    }
}
