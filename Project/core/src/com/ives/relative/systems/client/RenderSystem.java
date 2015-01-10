package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.client.Visual;

/**
 * Created by Ives on 3/12/2014.
 * This system renders every entity
 */
@Wire
public class RenderSystem extends EntityProcessingSystem {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Visual> visualMapper;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        //noinspection unchecked
        super(Aspect.getAspectForAll(Visual.class, Position.class));
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    protected void begin() {
        super.begin();
        Gdx.gl.glClearColor(0.5f, 0.9f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        positionCamera();

        batch.begin();
    }

    @Override
    protected void process(Entity entity) {
        Position position = mPosition.get(entity);

        float startX = camera.position.x - camera.viewportWidth / 2 - 2;
        float endX = camera.position.x + camera.viewportWidth / 2 + 2;
        float startY = camera.position.y - camera.viewportHeight / 2 - 2;
        float endY = camera.position.y + camera.viewportHeight / 2 + 2;

        if (position.x > startX && position.x < endX
                && position.y > startY && position.y < endY) {
            Visual visual = visualMapper.get(entity);
            //Upped the texture width and height to fill the gaps between blocks
            batch.draw(visual.texture,
                    position.x - visual.width / 2f,
                    position.y - visual.height / 2f,
                    visual.width / 2f, visual.height / 2f,
                    visual.width, visual.height,
                    1, 1,
                    position.rotation * MathUtils.radiansToDegrees);
        }
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    private void positionCamera() {
        Entity player = world.getManager(TagManager.class).getEntity("player");

        //If player has spawned
        if (player != null) {
            Position playerPosition = mPosition.get(player);
            camera.position.x = playerPosition.x;
            camera.position.y = playerPosition.y + 4;
            camera.update();
            batch.setProjectionMatrix(camera.combined);
        }
    }
}
