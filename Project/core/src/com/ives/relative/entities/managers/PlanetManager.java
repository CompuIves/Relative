package com.ives.relative.entities.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.planet.SeedComponent;
import com.ives.relative.entities.components.planet.WorldComponent;
import com.ives.relative.entities.factories.Planet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 11/12/2014.
 */
@Wire
public class PlanetManager extends Manager {
    /**
     * All entities that are mapped to a planet, with the planet as key.
     */
    private final Map<String, Entity> entitiesByPlanet;
    private final Map<Entity, String> planetsByEntity;
    private Planet planet;

    public PlanetManager() {
        entitiesByPlanet = new HashMap<String, Entity>();
        planetsByEntity = new HashMap<Entity, String>();
    }

    public void addPlanet(String name, Entity e) {
        entitiesByPlanet.put(name, e);
        planetsByEntity.put(e, name);
    }

    public Entity getPlanet(String name) {
        return entitiesByPlanet.get(name);
    }

    public String getPlanetID(Entity planet) {
        return planetsByEntity.get(planet);
    }

    public Entity createNewPlanet(String id, String name, String seed, World physicsWorld, int velocityIterations, int positionIterations) {
        Entity e = new EntityBuilder(world).with(new NameComponent(id, name), new SeedComponent(seed)).build();

        //TODO NOT THE WAY TO DO IT
        e.edit().add(new WorldComponent(physicsWorld, velocityIterations, positionIterations));
        addPlanet(id, e);
        return e;
    }

    public void generateTerrain(String planetID) {
        generateTerrain(entitiesByPlanet.get(planetID));
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

        tileManager.createTile(planet, 20, 15, 0, "dirt", true);
        tileManager.createTile(planet, 25, 15, 0, "dirt", true);
        tileManager.createTile(planet, 23, 20, 0, "dirt", true);
        tileManager.createTile(planet, 30, 13, 0, "dirt", true);
        tileManager.createTile(planet, 30, 18, 0, "dirt", true);
        tileManager.createTile(planet, 30, 30, 0, "dirt", true);
    }
}
