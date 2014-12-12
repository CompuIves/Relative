package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire()
public class MovementSystem extends EntityProcessingSystem {
    protected ComponentMapper<Physics> mBodyComponent;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Velocity> mVelocity;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public MovementSystem() {
        super(Aspect.getAspectForAll(Physics.class).one(Position.class, Velocity.class));
    }

    @Override
    protected void process(Entity e) {
        Vector2 bodyPosition = mBodyComponent.get(e).body.getPosition();
        Vector2 componentPosition = new Vector2(mPosition.get(e).x, mPosition.get(e).y);
        if (!bodyPosition.equals(componentPosition)) {
            componentPosition.x = bodyPosition.x;
            componentPosition.y = bodyPosition.y;
        }
        /*
        if(mVelocity.get(e) != null) {
            Vector2 bodyVelocity = mBodyComponent.get(e).body.getLinearVelocity();
            Vector2 componentVelocity = new Vector2(mVelocity.get(e).vx, mVelocity.get(e).vy);
            if(!bodyVelocity.equals(componentVelocity)) {
                componentVelocity.x = bodyVelocity.x;
                componentVelocity.y = bodyVelocity.y;
            }
        }
        */
    }
}
