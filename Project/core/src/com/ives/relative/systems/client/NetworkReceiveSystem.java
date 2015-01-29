package com.ives.relative.systems.client;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.entities.components.Authority;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.factories.PlayerFactory;
import com.ives.relative.factories.TileFactory;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.assets.tiles.SolidTile;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.handshake.planet.ChunkPacket;
import com.ives.relative.network.packets.requests.RequestEntity;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.systems.WorldSystem;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.universe.planets.TileManager;
import com.ives.relative.utils.ComponentUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ives on 22/12/2014.
 * <p/>
 * This system processes all entities received. The system makes sure that the entities get added by the main thread.
 */
@Wire
public class NetworkReceiveSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;
    protected AuthorityManager authorityManager;
    protected TileManager tileManager;
    protected ClientManager clientManager;
    protected ClientNetworkSystem clientNetworkSystem;
    protected TagManager tagManager;
    protected ChunkManager chunkManager;
    protected WorldSystem worldSystem;
    protected UuidEntityManager uuidEntityManager;
    protected UniverseManager universeManager;

    protected ComponentMapper<Velocity> mVelocity;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<Visual> mVisual;
    protected ComponentMapper<TileC> mTileC;
    protected ComponentMapper<Authority> mAuthority;
    protected ComponentMapper<NetworkC> mNetworkC;

    Map<Integer, Array<Component>> changedEntities;
    BlockingQueue<BasePacket> queue;

    public NetworkReceiveSystem() {
        changedEntities = new HashMap<Integer, Array<Component>>();
        queue = new LinkedBlockingQueue<BasePacket>();
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected void begin() {
        try {
            for (int i = 0; i < queue.size(); i++) {
                BasePacket p = queue.take();

                if (p instanceof CreateEntityPacket) {
                    CreateEntityPacket packet = (CreateEntityPacket) p;
                    processEntityPacket(packet);
                } else if (p instanceof ChunkPacket) {
                    ChunkPacket packet = (ChunkPacket) p;
                    UniverseBody ub = universeManager.getUniverseBody(((ChunkPacket) p).universeBody);
                    Chunk chunk = chunkManager.getChunk(ub, packet.x, packet.y);
                    chunk.changedTiles.putAll(packet.changedTiles);
                    chunkManager.chunkLoader.loadChunk(chunk);

                    for (CreateEntityPacket createEntityPacket : packet.entities) {
                        processEntityPacket(createEntityPacket);
                    }
                }

                //This prevents from this thread taking too long, it will continue next loop.
                if (i > 5)
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processEntityPacket(CreateEntityPacket packet) {
        Array<Component> componentArray = new Array<Component>();
        int id = packet.entityID;
        boolean delta = packet.delta;

        componentArray.addAll(packet.components);

        Entity e = addEntity(id, componentArray, delta);
        componentArray.clear();

        int playerID = world.getSystem(ClientNetworkSystem.class).getPlayerNetworkID();
        if (id == playerID) {
            e.edit().add(new InputC());
        }
    }

    public void addDataForProcessing(BasePacket packet) {
        queue.add(packet);
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

        NetworkC networkC = mNetworkC.get(e);
        finishEntity(e, networkC.type);
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
    //TODO move this overly coupled method to a nice handler. Use interface for different kinds of entities
    private void finishEntity(Entity entity, NetworkManager.Type type) {
        Gdx.app.debug("EntityReceived", "Received entity with type: " + type);
        Physics physics;
        Position position;
        Velocity velocity;
        Visual visual;
        switch (type) {
            case PLAYER:
                visual = mVisual.get(entity);
                visual.texture = new TextureRegion(new Texture("player.png"));

                physics = mPhysics.get(entity);
                velocity = mVelocity.get(entity);
                position = mPosition.get(entity);
                physics.body = PlayerFactory.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, worldSystem.physicsWorld);

                processBody(entity);
                Authority authority = mAuthority.get(entity);
                authorityManager.authorizeEntity(authority.getOwner(), entity, authority.type);

                tagManager.register("player", entity);
                clientNetworkSystem.registerPlayer(mNetworkC.get(entity).id);
                break;
            case TILE:
                physics = mPhysics.get(entity);
                visual = mVisual.get(entity);
                position = mPosition.get(entity);
                TileC tileC = mTileC.get(entity);
                SolidTile tile = tileManager.solidTiles.get(tileC.id);
                World physicsWorld = worldSystem.physicsWorld;

                physics.body = TileFactory.createBody(entity, tile, position.x, position.y, true, physicsWorld);

                if (physics.bodyType == BodyDef.BodyType.DynamicBody) {
                    processBody(entity);
                }
                visual.texture = tile.textureRegion;
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }

    private void processBody(Entity e) {
        Body body = mPhysics.get(e).body;
        body.setType(BodyDef.BodyType.DynamicBody);
        body.getFixtureList().get(0).setSensor(false);
        chunkManager.addEntityToChunk(e);
    }

    public void requestEntity(int id) {
        clientManager.network.sendObjectTCP(ClientNetwork.CONNECTIONID, new RequestEntity(id));
    }
}
