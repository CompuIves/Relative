package com.ives.relative.entities.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.components.VisualComponent;

/**
 * Created by Ives on 3/12/2014.
 */
public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        this.batch = batch;
        this.camera = camera;
        Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, VisualComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        float startX = camera.position.x - camera.viewportWidth / 2 - 2;
        float endX = camera.position.x + camera.viewportWidth / 2 + 2;
        float startY = camera.position.y - camera.viewportHeight / 2 - 2;
        float endY = camera.position.y + camera.viewportHeight / 2 + 2;

        batch.begin();
        for(Entity entity : entities) {
            Body body = Mappers.body.get(entity).body;
            if(body.getPosition().x > startX && body.getPosition().x < endX
                    && body.getPosition().y > startY && body.getPosition().y < endY) {
                VisualComponent visual = Mappers.visual.get(entity);
                batch.draw(new TextureRegion(visual.texture),
                        body.getPosition().x - visual.width / 2,
                        body.getPosition().y - visual.height / 2,
                        visual.width / 2, visual.height / 2,
                        visual.width, visual.height,
                        1, 1,
                        body.getTransform().getRotation() * MathUtils.radiansToDegrees);
            }
        }
        batch.end();
    }
}
