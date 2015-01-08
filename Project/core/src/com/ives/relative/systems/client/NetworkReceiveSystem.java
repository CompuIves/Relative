package com.ives.relative.systems.client;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.Player;
import com.ives.relative.factories.Tile;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.managers.planet.PlanetManager;
import com.ives.relative.managers.planet.TileManager;
import com.ives.relative.network.packets.handshake.RequestWorldSnapshot;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ives on 22/12/2014.
 *
 * This system processes all entities received. The system makes sure that the entities get added by the main thread.
 */
@Wire
public class NetworkReceiveSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;
    protected AuthorityManager authorityManager;

    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Position> mPosition;

    Map<Integer, Array<Component>> changedEntities;
    BlockingQueue<CreateEntityPacket> queue;

    public NetworkReceiveSystem() {
        changedEntities = new HashMap<Integer, Array<Component>>();
        queue = new LinkedBlockingQueue<CreateEntityPacket>();
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected void begin() {
        try {
            for (int i = 0; i < queue.size(); i++) {
                CreateEntityPacket packet = queue.take();
                int id = packet.entityID;
                NetworkManager.Type type = packet.type;
                boolean delta = packet.delta;

                Array<Component> componentArray = new Array<Component>();
                componentArray.addAll(packet.components);

                Entity e;
                if (type != null) {
                    e = addEntity(id, componentArray, delta, type);
                } else {
                    e = addEntity(id, componentArray, delta);
                }

                int playerID = world.getSystem(ClientNetworkSystem.class).getPlayerID();
                if (id == playerID) {
                    e.edit().add(new InputC());
                }

                //This prevents from this thread taking too long, it will continue next loop.
                if (i > 5)
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDataForProcessing(CreateEntityPacket createEntityPacket) {
        queue.add(createEntityPacket);
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
    public Entity addEntity(int id, Array<Component> components, boolean delta, NetworkManager.Type type) {
        Entity e = addEntity(id, components, delta);
        finishEntity(e, type);
        return e;
    }

    /**
     * When a remote entity is added there is a chance of duplicates, this method looks for the id and edits the
     * existing entity accordingly.
     *
     * @param id         id of the entity which needs to be changed
     * @param components the components of the old Entity
     * @param delta      should every component be removed before adding these components?
     */
    public Entity addEntity(int id, Array<Component> components, boolean delta) {
        Entity e;
        if (!networkManager.containsEntity(id)) {
            e = world.createEntity();
            addComponentsToEntity(e, components, delta);
            networkManager.addEntity(id, e);
        } else {
            e = networkManager.getEntity(id);
            addComponentsToEntity(e, components, delta);
        }
        return e;
    }

    /**
     * Adds the components to an entity.
     *
     * @param e
     * @param components
     * @param delta      if delta is false, every old component will get removed first.
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
     *
     * @param entity The entity to be finished
     * @param type   The type of the entity
     */
    private void finishEntity(Entity entity, NetworkManager.Type type) {
        Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
        Position position = entity.getWorld().getMapper(Position.class).get(entity);
        Visual visual = entity.getWorld().getMapper(Visual.class).get(entity);
        switch (type) {
            case PLAYER:
                physics = entity.getWorld().getMapper(Physics.class).get(entity);
                position = entity.getWorld().getMapper(Position.class).get(entity);
                Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
                Entity planet = entity.getWorld().getManager(PlanetManager.class).getPlanet(position.planet);

                physics.body = Player.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, 1f, planet);
                visual.texture = new TextureRegion(new Texture("player.png"));

                Authority authority = world.getMapper(Authority.class).get(entity);
                authorityManager.authorizeEntity(authority.owner, entity, authority.type);
                break;
            case TILE:
                TileC tileC = entity.getWorld().getMapper(TileC.class).get(entity);
                SolidTile tile = entity.getWorld().getManager(TileManager.class).solidTiles.get(tileC.id);
                com.badlogic.gdx.physics.box2d.World physicsWorld = entity.getWorld().getMapper(WorldC.class).get(entity.getWorld().getManager(PlanetManager.class).getPlanet(position.planet)).world;
                physics.body = Tile.createBody(entity, tile, position.x, position.y, true, physicsWorld);
                visual.texture = tile.textureRegion;
                break;
            case PLANET:
                Name name = world.getMapper(Name.class).get(entity);
                PlanetManager planetManager = world.getManager(PlanetManager.class);
                planetManager.addPlanet(name.internalName, entity);
                //world.getManager(ClientManager.class).network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestNearbyChunks());
                world.getManager(ClientManager.class).network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestWorldSnapshot());
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }
}
