package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class Box2DDebugRendererSystem extends IteratingSystem {
    Box2DDebugRenderer box2DDebugRenderer;
    OrthographicCamera camera;
    public Box2DDebugRendererSystem(Family family, OrthographicCamera camera) {
        super(family);
        box2DDebugRenderer = new Box2DDebugRenderer();
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        World world = Mappers.world.get(entity).world;
        box2DDebugRenderer.render(world, camera.combined);
    }
}
