package com.ives.relative.systems.client;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.client.PlayerConnectedEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.UniverseBody;

/**
 * Created by Ives on 1/2/2015.
 * Transforms every object to the players perspective
 */
@Wire
public class UniverseTransformSystem extends EntityProcessingSystem implements EntityEventObserver {
    protected TagManager tagManager;
    protected EventManager eventManager;

    protected ComponentMapper<Position> mPosition;

    private UniverseBody playerUBody;

    public UniverseTransformSystem() {
        super(Aspect.getAspectForAll(Position.class));

        //Wait until player connected and loaded
        setEnabled(false);
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    @Override
    protected void begin() {
        Entity player = tagManager.getEntity("player");
        Position position = mPosition.get(player);
        playerUBody = position.universeBody;
    }

    @Override
    protected void process(Entity e) {
        Position position = mPosition.get(e);
        if (!position.universeBody.equals(playerUBody)) {
            Vector3 transform = new Vector3(position.x, position.y, position.rotation);
            position.universeBody.transformVectorToUniverseBody(playerUBody, transform);
            position.x = transform.x;
            position.y = transform.y;
            position.rotation = position.z * MathUtils.degreesToRadians;
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof PlayerConnectedEvent)
            this.setEnabled(true);
    }
}
