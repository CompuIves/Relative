package com.ives.relative.systems.client;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.body.Location;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.client.PlayerConnectedEvent;
import com.ives.relative.managers.event.EventManager;

/**
 * Created by Ives on 11/2/2015.
 */
@Wire
public class CameraPositionSystem extends VoidEntitySystem implements EntityEventObserver {
    protected ComponentMapper<Physics> mPhysics;
    protected EventManager eventManager;
    Camera camera;


    public CameraPositionSystem(Camera camera) {
        this.camera = camera;

        //Wait until player connected and loaded
        setEnabled(false);
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    @Override
    protected void processSystem() {
        Entity player = world.getManager(TagManager.class).getEntity("player");

        Vector2 playerPosition = mPhysics.get(player).body.getPosition();
        camera.position.x = playerPosition.x;
        camera.position.y = playerPosition.y + 4;
        camera.update();
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof PlayerConnectedEvent)
            setEnabled(true);
    }
}
