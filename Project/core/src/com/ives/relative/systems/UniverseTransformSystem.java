package com.ives.relative.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Transform;
import com.ives.relative.entities.events.EntityEvent;
import com.ives.relative.entities.events.EntityEventObserver;
import com.ives.relative.entities.events.position.UniverseBodyCollisionEvent;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.utils.BodyUtils;

/**
 * Created by Ives on 7/2/2015. <p></p>
 * <p/>
 * <i>System to handle transformation glitches between UniverseBodies and Entities.</i> <br></br>
 * When a body is on the border of a UniverseBody this system will create two bodies, one in UniverseBody A and one in
 * UniverseBody B. This means that the Entity can collide in both UniverseBodies. When the Entity joins a UniverseBody
 * it will remove the 'wrong' body. <br></br>
 * The two bodies get synced with eachother by copying over its positions. This happens from both sides changing each time.
 * For example the first step the positions of the first body get copied over to the second body, the second step the
 * positions of the second body get copied over to the first body and so on.
 */
@Wire
public class UniverseTransformSystem extends EntityProcessingSystem implements EntityEventObserver {
    protected EventManager eventManager;
    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<Transform> mTransform;
    private boolean transformFirstBody = true;

    public UniverseTransformSystem() {
        super(Aspect.getAspectForAll(Transform.class, Physics.class, Position.class));
    }

    @Override
    protected void initialize() {
        eventManager.addObserver(this);
    }

    /**
     * Transforms entity if it is on a border. Also checks if an entity enters a universebody or not.
     *
     * @param e
     */
    @Override
    protected void process(Entity e) {
        Physics physics = mPhysics.get(e);
        if (physics.secondBody != null) {
            Position p = mPosition.get(e);
            Transform transform = mTransform.get(e);

            //Transforms the given body to the position of the other. This switches each time so the first body also
            //responds to collision of the secondbody.
            if (transformFirstBody) {
                BodyUtils.transformBody(physics.body, physics.secondBody, p.universeBody, physics.secondUniverseBody);
                //transformFirstBody = false;
            }

            if (isInUniverseBody(physics.body.getPosition(), physics.body.getAngle(), transform, p.universeBody)) {
                removeSecondaryBody(e, p.universeBody);
            } else if (isInUniverseBody(physics.secondBody.getPosition(), physics.secondBody.getAngle(), transform, physics.secondUniverseBody)) {
                removeSecondaryBody(e, physics.secondUniverseBody);
            }
        }

    }

    /**
     * Checks if the position with its width and height is fully in the UniverseBody described
     *
     * @param pos          position of entity
     * @param universeBody universebody to check in
     * @param transform    width and height of entity
     * @return if entity is fully in universebody
     */
    private boolean isInUniverseBody(Vector2 pos, float angle, Transform transform, UniverseBody universeBody) {
        float halfwidth = (transform.width * MathUtils.cos(angle) + transform.height * -MathUtils.sin(angle)) / 2;
        float halfheight = (transform.height * MathUtils.cos(angle) + transform.width * MathUtils.sin(angle)) / 2;

        Vector2 tempVec = new Vector2();

        return universeBody.isAtPoint(tempVec.set(Math.abs(pos.x + halfwidth), Math.abs(pos.y + halfheight))) &&
                universeBody.isAtPoint(tempVec.set(Math.abs(pos.x - halfwidth), Math.abs(pos.y - halfheight)));
    }

    /**
     * Removes the appropriate body of the entity, this can be the secondary body and the first body. When the first
     * body gets removed this will also handle the UniverseBody change and body change.
     *
     * @param e               Entity
     * @param newUniverseBody The UniverseBody the entity is in
     */
    private void removeSecondaryBody(Entity e, UniverseBody newUniverseBody) {
        Physics physics = mPhysics.get(e);
        if (physics.secondBody != null) {
            Position p = mPosition.get(e);

            System.out.println(physics.body);
            if (p.universeBody.equals(newUniverseBody)) {
                Gdx.app.log("UniverseTransformSystem", "Removing " + e + " from " + physics.secondUniverseBody);
                //This means the player is back in its previous body
                physics.secondUniverseBody.removeBody(physics.secondBody);
                physics.secondUniverseBody = null;
                physics.secondBody = null;
            } else {
                Gdx.app.log("UniverseTransformSystem", "Removing " + e + " from " + p.universeBody);
                Vector3 pos = new Vector3(p.x, p.y, p.rotation);
                p.universeBody.transformVectorToUniverseBody(newUniverseBody, pos);
                p.x = pos.x;
                p.y = pos.y;
                p.rotation = pos.z;

                p.universeBody.removeBody(physics.body);
                p.universeBody = newUniverseBody;
                physics.secondUniverseBody = null;
                physics.body = physics.secondBody;
                physics.secondBody = null;
            }
            System.out.println(physics.body);
            System.out.println(p.universeBody);
        }
    }

    /**
     * Creates a secondary body when an Entity is on the border between two UniverseBodies, this will make the body
     * collide in both UniverseBodies.
     *
     * @param e
     * @param toUniverseBody
     */
    private void createSecondaryBody(Entity e, UniverseBody toUniverseBody) {
        Physics physics = mPhysics.get(e);
        if (physics.secondBody == null) {
            Gdx.app.log("UniverseTransformSystem", "Creating Second Body for " + e + " in " + toUniverseBody);
            Position p = mPosition.get(e);

            Body fromBody = physics.body;
            UniverseBody fromUniverseBody = p.universeBody;

            Body secondBody = BodyUtils.copyBody(toUniverseBody.world, fromBody);
            BodyUtils.transformBody(fromBody, secondBody, fromUniverseBody, toUniverseBody);

            physics.secondBody = secondBody;
            physics.secondUniverseBody = toUniverseBody;
        }
    }

    @Override
    public void onNotify(EntityEvent event) {
        if (event instanceof UniverseBodyCollisionEvent) {
            System.out.println("RECEIVED COLLISION");
            createSecondaryBody(event.entity, ((UniverseBodyCollisionEvent) event).universeBody);
        }
    }
}
