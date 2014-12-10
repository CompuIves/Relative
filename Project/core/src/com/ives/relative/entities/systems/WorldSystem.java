package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.components.planet.WorldComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 4/12/2014.
 */
public class WorldSystem extends IntervalIteratingSystem {
    float interval;

    public WorldSystem(Family family, float interval) {
        super(family, interval);
        this.interval = interval;
    }

    @Override
    protected void processEntity(Entity entity) {
        WorldComponent worldComponent = Mappers.world.get(entity);
        worldComponent.world.step(interval, worldComponent.velocityIterations, worldComponent.positionIterations);
    }

    public Entity getPlanet(String id) {
        Entity e = null;
        for(Entity entity : getEntities()) {
            if(Mappers.name.get(entity).internalName.equals(id)) {
                e = entity;
            }
        }
        return e;
    }

    public Entity getPlanet(World world) {
        Entity e = null;
        for (Entity entity : getEntities()) {
            if (Mappers.world.get(entity).world.equals(world)) {
                e = entity;
            }
        }
        return e;
    }

    public List<Entity> getDynamicEntitiesOnWorld(String id) {
        Array<Body> bodies = new Array<Body>();
        List<Entity> entities = new ArrayList<Entity>();
        Mappers.world.get(getPlanet(id)).world.getBodies(bodies);
        for (Body body : bodies) {
            if (body.getType() == BodyDef.BodyType.DynamicBody)
                entities.add((Entity) body.getUserData());
        }
        return entities;
    }
}
