package com.ives.relative.systems.client;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.client.ClientManager;
import com.ives.relative.core.client.ClientNetwork;
import com.ives.relative.core.client.Player;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.events.client.PlayerConnectedEvent;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.network.packets.BasePacket;
import com.ives.relative.network.packets.handshake.planet.ChunkPacket;
import com.ives.relative.network.packets.requests.RequestEntity;
import com.ives.relative.network.packets.updates.CreateEntityPacket;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;
import com.ives.relative.utils.ComponentUtils;

import java.util.ArrayList;
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
    protected ClientManager clientManager;
    protected ChunkManager chunkManager;
    protected UuidEntityManager uuidEntityManager;
    protected UniverseSystem universeSystem;
    protected TagManager tagManager;
    protected EventManager eventManager;

    protected ComponentMapper<Name> mName;

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
                    UniverseBody ub = universeSystem.getUniverseBody(((ChunkPacket) p).universeBody);
                    Chunk chunk = chunkManager.getChunk(ub, new Vector2(packet.x, packet.y));
                    chunk.changedTiles.putAll(packet.changedTiles);
                    chunkManager.loadChunk(chunk);

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

        addEntity(id, componentArray, delta);
        componentArray.clear();
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
        postProcessEntity(e, components);
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
     * Converts the components from networkable components to usable components again
     * @param e
     * @param components
     */
    private void postProcessEntity(Entity e, Array<Component> components) {
        NetworkC networkC = world.getMapper(NetworkC.class).get(e);

        Array<String> loadedComponents = new Array<String>();
        ArrayList<CustomNetworkComponent> customNetworkComponents = new ArrayList<CustomNetworkComponent>();

        for (Component c : components) {
            if (c instanceof CustomNetworkComponent)
                customNetworkComponents.add((CustomNetworkComponent) c);
        }

        try {
            LinkedBlockingQueue<CustomNetworkComponent> queue = new LinkedBlockingQueue();
            queue.addAll(customNetworkComponents);

            CustomNetworkComponent ce;
            int tries = 0;

            //This can be called more times since sometimes a component can't be loaded because its dependants aren't loaded
            //yet, this means that that component will be skipped one time. Then the loop gets called for a second time etc.
            queueLoop:
            while ((ce = queue.poll()) != null) {
                for (String k : ce.dependants) {
                    if (!loadedComponents.contains(k, false)) {
                        //Dependents are not yet loaded, add this at the end of the queue for processing.
                        queue.add(ce);
                        continue queueLoop;
                    }
                }

                ce.convertForReceiving(e, world, networkC.type);
                loadedComponents.add(ce.getClass().getSimpleName());

                tries++;
                if (tries > 20)
                    throw new StackOverflowError();
            }
        } catch (StackOverflowError ex) {
            Gdx.app.error("EntityReceive", "Couldn't add entity: " + e.toString() + " because its components have wrong dependants");
            ComponentUtils.removeEntity(e);
            return;
        }

        finalProcess(e, networkC);
        Gdx.app.debug("EntityReceive", "Received entity with type: " + networkC.type);
    }

    private void finalProcess(Entity e, NetworkC networkC) {
        switch (networkC.type) {
            case PLAYER:
                if (mName.get(e).internalName.equals(Player.ID)) {
                    tagManager.register("player", e);
                    e.edit().add(new InputC());
                    Player.NETWORK_ID = networkC.id;

                    eventManager.notifyEvent(eventManager.getEvent(PlayerConnectedEvent.class, e));
                }
                break;
        }

        chunkManager.addEntityToChunk(e);
    }

    public void requestEntity(int id) {
        clientManager.network.sendObjectTCP(ClientNetwork.CONNECTION_ID, new RequestEntity(id));
    }
}
