package com.ives.relative.systems;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class Box2DDebugRendererSystem extends VoidEntitySystem {
    protected WorldSystem worldSystem;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthographicCamera camera;

    public Box2DDebugRendererSystem(OrthographicCamera camera) {
        super();
        box2DDebugRenderer = new Box2DDebugRenderer();
        box2DDebugRenderer.setDrawVelocities(true);
        this.camera = camera;
    }

    @Override
    protected void processSystem() {
        box2DDebugRenderer.render(worldSystem.physicsWorld, camera.combined);
    }
}
