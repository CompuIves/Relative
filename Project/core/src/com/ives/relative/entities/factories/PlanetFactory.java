package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.planet.GravityComponent;
import com.ives.relative.entities.components.planet.SeedComponent;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 12/1/2014.
 */
public class PlanetFactory extends Factory{

    public Entity createPlanet(String id, String name, Vector2 gravity, int velocityIterations, int positionIterations, String seed) {
        Entity e = new Entity();
        e.add(new NameComponent(id, name));
        e.add(new GravityComponent(gravity.x, gravity.y));
        World world = new World(gravity, true);
        e.add(new WorldComponent(world, velocityIterations, positionIterations));
        e.add(new SeedComponent(seed));
        return e;
    }

}
