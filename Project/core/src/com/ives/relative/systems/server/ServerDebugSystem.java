package com.ives.relative.systems.server;

import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public class ServerDebugSystem extends VoidEntitySystem {
    protected UniverseSystem universeSystem;
    private ShapeRenderer shapeRenderer;

    private ServerBox2DDebugRenderer serverBox2DDebugRenderer;
    private OrthographicCamera camera;

    public ServerDebugSystem(OrthographicCamera camera) {
        super();
        serverBox2DDebugRenderer = new ServerBox2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }

    @Override
    protected void processSystem() {
        UniverseBody ubody = universeSystem.getUniverseBody("andromeda");
        drawUniverseBody(ubody, camera.combined);
    }

    private void drawUniverseBody(UniverseBody universeBody, Matrix4 viewport) {
        Color color = Colors.getColors().values().toArray().get(universeBody.depth);
        drawChunks(universeBody, viewport, color);

        serverBox2DDebugRenderer.UNIVERSE_BODY_COLOR.set((universeBody.depth / 5f), 0, (1 - (universeBody.depth / 5f)), 1);
        serverBox2DDebugRenderer.render(universeBody, viewport);
        drawChildren(universeBody, viewport);
    }

    private void drawChildren(UniverseBody universeBody, Matrix4 initialViewport) {
        for (UniverseBody child : universeBody.getChildren()) {
            Vector3 translation = new Vector3(child.getPosition().x, child.getPosition().y, 0);
            Matrix4 newViewport = initialViewport.cpy().translate(translation).rotate(new Vector3(0, 0, 1), child.getRotation());
            drawUniverseBody(child, newViewport);
        }
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
}
