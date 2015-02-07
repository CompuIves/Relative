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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.client.PlayerConnectedEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;

/**
 * Created by Ives on 3/12/2014.
 * This system renders every entity
 */
@Wire
public class RenderSystem extends EntityProcessingSystem implements EntityEventObserver {
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Visual> visualMapper;

    protected ChunkManager chunkManager;
    protected EventManager eventManager;
    protected TagManager tagManager;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Entity player;
    private Position playerPos;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        //noinspection unchecked
        super(Aspect.getAspectForAll(Visual.class, Position.class));
        this.batch = batch;
        this.camera = camera;

        //Wait until player connected and loaded
        setEnabled(false);
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    @Override
    protected void begin() {
        super.begin();
        //Gets the player info
        getPlayer();
        //Transforms every position outside player uBody (relative!)
        transformPositions();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        positionCamera();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderBackground();
    }

    @Override
    protected void process(Entity entity) {
        Position position = mPosition.get(entity);
        Visual visual = visualMapper.get(entity);
        batch.draw(visual.texture,
                position.x - visual.width / 2, position.y - visual.height / 2,
                visual.width / 2, visual.height / 2,
                visual.width, visual.height,
                1.02f, 1.02f,
                position.rotation);
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    private void getPlayer() {
        player = tagManager.getEntity("player");
        playerPos = mPosition.get(player);
    }

    private void positionCamera() {
        Entity player = world.getManager(TagManager.class).getEntity("player");

        Position playerPosition = mPosition.get(player);
        camera.position.x = playerPosition.x;
        camera.position.y = playerPosition.y + 4;

        //float rotation = playerPosition.rotation * MathUtils.radiansToDegrees;
        //float camrotation = -getCurrentCameraRotation() + 180;
        //camera.rotate(camrotation - rotation + 180);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void renderBackground() {
        Vector3 pos = new Vector3();
        for (Chunk chunk : chunkManager.getLoadedChunks()) {
            if (chunk.bgColor != null && chunk.texture == null) {
                chunk.texture = new Texture(chunk.bgColor);
            }

            if (chunk.texture != null) {
                pos.x = chunk.x;
                pos.y = chunk.y;
                if(!chunk.universeBody.equals(playerPos.universeBody)) {
                    chunk.universeBody.transformVectorToUniverseBody(playerPos.universeBody, pos);
                }

                batch.draw(chunk.texture, pos.x, pos.y, chunk.width, chunk.height);
            }
        }
    }

    private void transformPositions() {
        for(Entity e : getActives()) {
            transformPosition(e);
        }
    }

    private void transformPosition(Entity e) {
        Vector3 transform = new Vector3();
        Position position = mPosition.get(e);
        if (!position.universeBody.equals(playerPos.universeBody)) {
            transform.set(position.x, position.y, position.rotation);
            position.universeBody.transformVectorToUniverseBody(playerPos.universeBody, transform);
            position.x = transform.x;
            position.y = transform.y;
            position.rotation = position.z;
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof PlayerConnectedEvent)
            this.setEnabled(true);
    }
}
