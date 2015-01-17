package com.ives.relative.managers.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.*;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.planet.chunks.ChunkManager;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Ives on 11/12/2014.
 * Manager of the planets! Sort of an Interstellar Manager.
 */
@Wire
public class PlanetManager extends Manager {
    /**
     * All entities that are mapped to a planet, with the planet as key.
     */
    private final Map<String, UUID> entitiesByPlanet;
    private final Map<UUID, String> planetsByEntity;

    protected CollisionManager collisionManager;
    protected NetworkManager networkManager;
    protected UuidEntityManager uuidEntityManager;
    protected ChunkManager chunkManager;

    protected ComponentMapper<WorldC> mWorldC;
    protected ComponentMapper<PPosition> mPPosition;
    protected ComponentMapper<Size> mSize;

    public PlanetManager() {
        entitiesByPlanet = new HashMap<String, UUID>();
        planetsByEntity = new HashMap<UUID, String>();
    }

    public void addPlanet(String name, Entity e) {
        if (!planetsByEntity.containsKey(e)) {
            if (entitiesByPlanet.containsKey(name)) {
                UUID old = entitiesByPlanet.remove(name);
                planetsByEntity.remove(old);
            }

            entitiesByPlanet.put(name, e.getUuid());
            planetsByEntity.put(e.getUuid(), name);
            mWorldC.get(e).world.setContactListener(collisionManager);

            world.getManager(GroupManager.class).add(e, "planets");
        }
    }

    public Entity getPlanet(String name) {
        if (entitiesByPlanet.containsKey(name))
            return uuidEntityManager.getEntity(entitiesByPlanet.get(name));
        else
            return null;
    }

    public Entity getPlanet(int x, int y) {
        //TODO dynamic planet loading
        for (UUID planetUUID : planetsByEntity.keySet()) {
            Entity planet = uuidEntityManager.getEntity(planetUUID);
            PPosition position = mPPosition.get(planet);
            Size size = mSize.get(planet);

            int startX = RelativeMath.fastfloor(position.x - size.width / 2);
            int startY = RelativeMath.fastfloor(position.y - size.height / 2);
            if (RelativeMath.isInBounds(x, startX, startX + size.width) && RelativeMath.isInBounds(y, startY, startY + size.height)) {
                return planet;
            }
        }
        return null;
    }

    public String getPlanetID(Entity planet) {
        return planetsByEntity.get(planet.getUuid());
    }

    /**
     * Creates a planet and adds it to the planet registry. The {@link com.badlogic.gdx.physics.box2d.World} created for physics (Box2D) has a gravity of
     * 0,0. This way the server doesn't have to simulate any physics. The planet has a separate gravity component which describes the gravity. When the
     * planet arrives on the planet the client will make a world with the gravity specified in the gravity component.
     *
     * @param id                 ID of the planet, the planet gets referenced by this ID.
     * @param name               The name of the planet, the players will see this name.
     * @param seed               A seed which determines how the planet will look like. Purely a random value would suffice.
     * @param gravity            Gravity on the planet.
     * @param width              The width in chunks of the planet
     * @param height             The height in chunks of the planet
     * @param velocityIterations This value is used for the physics, it will determine how many times each step the velocity is updated
     * @param positionIterations Same as velocityIterations, but for position.
     * @return Return the entity of the planet
     */
    public Entity createNewPlanet(String id, String name, String seed, Vector2 gravity, int width, int height, int velocityIterations, int positionIterations) {
        //Create the planet
        Entity e = new EntityBuilder(world).with(new Name(id, name),
                new Seed(seed),
                new PGravity(gravity.x, gravity.y),
                new WorldC(new World(new Vector2(0, 0), true), velocityIterations, positionIterations),
                new ChunkC(),
                new PPosition(0, 0),
                new Size(width, height))
                .group("planets")
                .build();

        chunkManager.generateChunkMap(e);

        //Get the networkID from the networkmanager
        int networkID = networkManager.addEntity(e);
        //Add a networkComponent to the planet
        e.edit().add(new NetworkC(networkID, -1, NetworkManager.Type.PLANET));

        //Add the planet to the planetmanager
        addPlanet(id, e);
        return e;
    }
}
