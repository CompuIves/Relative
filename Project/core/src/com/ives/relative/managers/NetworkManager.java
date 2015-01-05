package com.ives.relative.managers;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.Player;
import com.ives.relative.factories.Tile;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.managers.planet.ChunkManager;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.managers.planet.TileManager;
import com.ives.relative.network.packets.updates.ComponentPacket;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 13/12/2014.
 * This keeps the database of every networked entity and their network ID. It also processes incoming entities
 */
@Wire
public class NetworkManager extends Manager {
    protected ComponentMapper<NetworkC> mNetworkC;
    protected UuidEntityManager uuidEntityManager;
    protected ChunkManager chunkManager;
    Map<Integer, UUID> networkEntities;
    Map<UUID, Integer> networkIDs;
    int freeID;
    Array<Integer> removedIDs;

    public NetworkManager() {
        networkEntities = new HashMap<Integer, UUID>();
        networkIDs = new HashMap<UUID, Integer>();

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

    /**
     * Add an entity to the networkmanager.
     *
     * @param id
     * @param e
     */
    public void addEntity(int id, Entity e) {
        if (!networkEntities.containsKey(id) && !networkIDs.containsKey(uuidEntityManager.getUuid(e))) {
            networkEntities.put(id, uuidEntityManager.getUuid(e));
            networkIDs.put(uuidEntityManager.getUuid(e), id);
            chunkManager.addEntity(e);
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
        if (!networkEntities.containsKey(id)) {
            e = world.createEntity();
            addComponentsToEntity(e, components, delta);
            addEntity(id, e);
        } else {
            e = uuidEntityManager.getEntity(networkEntities.get(id));
            addComponentsToEntity(e, components, delta);
        }
        return e;
    }

    /**
     * Adds the components to an entity.
     * @param e
     * @param components
     * @param delta if delta is false, every old component will get removed first.
     * @return The entity which has been updated.
     */
    public Entity addComponentsToEntity(Entity e, Array<Component> components, boolean delta) {
        if (!delta) {
            ComponentUtils.removeAllComponents(e);
        }
        ComponentUtils.addComponents(e, components);
        return e;
    }

    /**
     * This finishes a special entity by type. It creates a special body for example for tiles and for players.
     * @param entity The entity to be finished
     * @param type The type of the entity
     */
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

                physics.body = Player.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, 1f, planet);
                visual.texture = new TextureRegion(new Texture("player.png"));
                break;
            case TILE:
                TileC tileC = entity.getWorld().getMapper(TileC.class).get(entity);
                SolidTile tile = entity.getWorld().getManager(TileManager.class).solidTiles.get(tileC.id);
                com.badlogic.gdx.physics.box2d.World physicsWorld = entity.getWorld().getMapper(WorldC.class).get(entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID)).world;
                physics.body = Tile.createBody(entity, tile, position.x, position.y, true, physicsWorld);
                visual.texture = tile.textureRegion;
                break;
            case PLANET:
                Name name = world.getMapper(Name.class).get(entity);
                PlanetManager planetManager = world.getManager(PlanetManager.class);
                planetManager.addPlanet(name.internalName, entity);
                planetManager.generateTerrain(entity);
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }

    /**
     * Remove networked entity from world and database
     * @param id id of networked entity
     */
    public void removeEntity(int id) {
        Entity e = uuidEntityManager.getEntity(networkEntities.get(id));
        if (e != null) {
            ComponentUtils.removeAllSpecialComponents(e);
            removedIDs.add(id);

            networkEntities.remove(id);
            networkIDs.remove(uuidEntityManager.getUuid(e));

            chunkManager.removeEntity(e);
            System.out.println("Removed entity: " + e.getId());
            e.deleteFromWorld();
        }
    }

    public Entity getEntity(int id) {
        if (networkEntities.containsKey(id)) {
            return uuidEntityManager.getEntity(networkEntities.get(id));
        } else {
            return null;
        }
    }

    public boolean containsEntity(int id) {
        return networkEntities.containsKey(id);
    }

    public ComponentPacket generateFullComponentPacket(Entity e) {
        Type type = mNetworkC.get(e).type;
        return generateFullComponentPacket(e, type);
    }

    /**
     * This generates a full packet of the desired entity, this packet can be sent to the client or server and the client/
     * server will add this entity to it.
     * @param e
     * @param type
     * @return
     */
    public ComponentPacket generateFullComponentPacket(Entity e, Type type) {
        Array<Component> components = ComponentUtils.getComponents(e);
        int id = getNetworkID(e);
        return new ComponentPacket(components, id, false, -1, type);
    }

    /**
     * Get the network ID of the entity
     * @param e
     * @return
     */
    public int getNetworkID(Entity e) {
        if (e != null && networkIDs.containsKey(uuidEntityManager.getUuid(e)))
            return networkIDs.get(uuidEntityManager.getUuid(e));
        else
            return -1;
    }

    /**
     * Gets free ID available, this only gets executed from the server
     * @return free ID
     */
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
