package com.ives.relative.managers;

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
import com.ives.relative.entities.components.planet.Gravity;
import com.ives.relative.entities.components.planet.Seed;
import com.ives.relative.entities.components.planet.WorldC;

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

    protected ComponentMapper<WorldC> mWorldC;
    protected ComponentMapper<Gravity> mGravity;

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

            if (mWorldC.get(e).world == null) {
                Gravity gravity = mGravity.get(e);
                mWorldC.get(e).world = new World(new Vector2(gravity.x, gravity.y), false);
            }

            mWorldC.get(e).world.setContactListener(collisionManager);

            world.getManager(GroupManager.class).add(e, "planets");
        }
    }

    public Entity getPlanet(String name) {
        return uuidEntityManager.getEntity(entitiesByPlanet.get(name));
    }

    public String getPlanetID(Entity planet) {
        return planetsByEntity.get(planet.getUuid());
    }

    public Entity createNewPlanet(String id, String name, String seed, World physicsWorld, int velocityIterations, int positionIterations) {
        //Create the planet
        Entity e = new EntityBuilder(world).with(new Name(id, name),
                new Seed(seed),
                new Gravity(physicsWorld.getGravity().x, physicsWorld.getGravity().y),
                new WorldC(physicsWorld, velocityIterations, positionIterations))
                .group("planets")
                .build();

        //Get the networkID from the networkmanager
        int networkID = networkManager.addEntity(e);
        //Add a networkComponent to the planet
        e.edit().add(new NetworkC(networkID, -1, NetworkManager.Type.PLANET));

        //Add the planet to the planetmanager
        addPlanet(id, e);
        return e;
    }

    public void generateTerrain(String planetID) {
        generateTerrain(uuidEntityManager.getEntity(entitiesByPlanet.get(planetID)));
    }

    public void generateTerrain(Entity planet) {
        TileManager tileManager = world.getManager(TileManager.class);
        for (int y = 1; y < 7; y++) {
            for (int x = 0; x < 200; x++)
                tileManager.createTile(planet, x, y, 0, "dirt", false);
        }
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 200; x++) {
                tileManager.createTile(planet, x, y, 0, "bedrock", false);
            }
        }
    }
}
