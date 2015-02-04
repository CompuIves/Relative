package com.ives.relative.systems;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class Box2DDebugRendererSystem extends VoidEntitySystem {
    protected UniverseSystem universeSystem;
    private ShapeRenderer shapeRenderer;

    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthographicCamera camera;

    public Box2DDebugRendererSystem(OrthographicCamera camera) {
        super();
        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }

    @Override
    protected void begin() {
        super.begin();

    }

    @Override
    protected void processSystem() {
        UniverseBody ubody = universeSystem.getUniverseBody("ivesolaria");
        drawChunks(ubody, camera.combined, Color.LIGHT_GRAY);
        box2DDebugRenderer.render(ubody.world, camera.combined);
        UniverseBody earth = universeSystem.getUniverseBody("ives");
        Vector3 translation = new Vector3(earth.getPosition().x, earth.getPosition().y, 0);
        Matrix4 newCamera = camera.combined.cpy().translate(translation).rotate(new Vector3(0, 0, 1), earth.getRotation());
        drawChunks(earth, newCamera, Color.PINK);
        box2DDebugRenderer.render(earth.world, newCamera);
    }

    private synchronized void drawChunks(UniverseBody universeBody, Matrix4 projection, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.setProjectionMatrix(projection);
        for (Chunk chunk : universeBody.chunks.values()) {
            if (chunk.loaded)
                shapeRenderer.rect(chunk.x, chunk.y, chunk.width, chunk.height);
        }
        shapeRenderer.end();
    }

    @Override
    protected void end() {
        super.end();

    }
}
