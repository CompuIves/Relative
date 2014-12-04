package com.ives.relative.planet;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;

/**
 * Created by Ives on 12/1/2014.
 */
public class PlanetFactory {
    TerrainGenerator terrainGenerator;
    GameManager game;

    public PlanetFactory(GameManager game) {
        this.game = game;
        this.terrainGenerator = new TerrainGenerator(game);
    }

    public Entity createPlanet(String id, String name, Vector2 gravity, int velocityIterations, int positionIterations) {
        Entity e = new Entity();
        e.add(new NameComponent(id, name));
        World world = new World(gravity, true);
        e.add(new WorldComponent(world, velocityIterations, positionIterations));

        terrainGenerator.generateTerrain(e);

        game.engine.addEntity(e);
        return e;
    }



}
