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
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.ChunkC;
import com.ives.relative.entities.components.planet.Gravity;
import com.ives.relative.entities.components.planet.Seed;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.event.EventManager;

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
    protected EventManager eventManager;
    protected ChunkManager chunkManager;

    protected ComponentMapper<WorldC> mWorldC;
    protected ComponentMapper<Gravity> mGravity;
    protected ComponentMapper<Position> mPosition;

    public PlanetManager() {
        entitiesByPlanet = new HashMap<String, UUID>();
        planetsByEntity = new HashMap<UUID, String>();
    }

    @Override
    protected void initialize() {
        super.initialize();
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

    public World createWorld(Gravity gravity) {
        return new World(new Vector2(gravity.x, gravity.y), true);
    }

    public Entity getPlanet(String name) {
        if (entitiesByPlanet.containsKey(name))
            return uuidEntityManager.getEntity(entitiesByPlanet.get(name));
        else
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
     * @param velocityIterations This value is used for the physics, it will determine how many times each step the velocity is updated
     * @param positionIterations Same as velocityIterations, but for position.
     * @return Return the entity of the planet
     */
    public Entity createNewPlanet(String id, String name, String seed, Vector2 gravity, int velocityIterations, int positionIterations) {
        //Create the planet
        Entity e = new EntityBuilder(world).with(new Name(id, name),
                new Seed(seed),
                new Gravity(gravity.x, gravity.y),
                new WorldC(new World(new Vector2(gravity.x, gravity.y), true), velocityIterations, positionIterations),
                new ChunkC())
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
}
