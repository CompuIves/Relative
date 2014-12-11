package com.ives.relative.entities.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.body.PhysicsPosition;

/**
 * Created by Ives on 3/12/2014.
 */
@Wire
public class RenderSystem extends EntitySystem {
    ComponentMapper<PhysicsPosition> bodyMapper;
    ComponentMapper<VisualComponent> visualMapper;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Aspect.getAspectForAll(VisualComponent.class, PhysicsPosition.class));
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        positionCamera();

        float startX = camera.position.x - camera.viewportWidth / 2 - 2;
        float endX = camera.position.x + camera.viewportWidth / 2 + 2;
        float startY = camera.position.y - camera.viewportHeight / 2 - 2;
        float endY = camera.position.y + camera.viewportHeight / 2 + 2;

        batch.begin();
        for(Entity entity : entities) {
            Body body = bodyMapper.get(entity).body;
            if(body.getPosition().x > startX && body.getPosition().x < endX
                    && body.getPosition().y > startY && body.getPosition().y < endY) {
                VisualComponent visual = visualMapper.get(entity);

                //TODO upped texture width and height against gaps, way to do it?
                batch.draw(visual.texture,
                        body.getPosition().x - visual.width / 2f,
                        body.getPosition().y - visual.height / 2f,
                        visual.width / 2f, visual.height / 2f,
                        visual.width + 0.01f, visual.height + 0.01f,
                        1, 1,
                        body.getTransform().getRotation() * MathUtils.radiansToDegrees);
            }
        }
        batch.end();
    }

    private void positionCamera() {
        ImmutableBag<Entity> playerEntities = world.getSystem(InputSystem.class).getActives();
        if(playerEntities.size() != 0) {
            Entity player = playerEntities.get(0);
            Body playerBody = bodyMapper.get(player).body;
            camera.position.x = playerBody.getPosition().x;
            camera.position.y = playerBody.getPosition().y + 4;
        }
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
}
