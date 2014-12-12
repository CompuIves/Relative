package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class Box2DDebugRendererSystem extends EntityProcessingSystem {
    protected ComponentMapper<WorldComponent> mWorldComponent;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthographicCamera camera;

    public Box2DDebugRendererSystem(OrthographicCamera camera) {
        super(Aspect.getAspectForAll(WorldComponent.class));
        box2DDebugRenderer = new Box2DDebugRenderer();
        this.camera = camera;
    }

    @Override
    protected void process(Entity e) {
        World world = mWorldComponent.get(e).world;
        box2DDebugRenderer.render(world, camera.combined);
    }
}
