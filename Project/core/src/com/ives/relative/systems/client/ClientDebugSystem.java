package com.ives.relative.systems.client;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.ives.relative.core.client.Player;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 6/2/2015.
 */
@Wire
public class ClientDebugSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;
    protected ComponentMapper<Position> mPosition;
    Camera camera;
    Box2DDebugRenderer box2DDebugRenderer;

    public ClientDebugSystem(OrthographicCamera camera) {
        this.camera = camera;
        box2DDebugRenderer = new Box2DDebugRenderer();
    }

    @Override
    protected void processSystem() {
        if (networkManager.getEntity(Player.NETWORK_ID) != null) {
            Entity player = networkManager.getEntity(Player.NETWORK_ID);
            Position pos = mPosition.get(player);

            box2DDebugRenderer.render(pos.space.world, camera.combined);
        }
    }
}
