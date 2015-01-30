package com.ives.relative.systems.planet;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.UuidEntityManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Gravity;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.ChunkManager;

import java.util.UUID;

/**
 * Created by Ives on 14/1/2015.
 */
@Wire
public class GravitySystem extends VoidEntitySystem {
    protected ChunkManager chunkManager;
    protected UuidEntityManager uuidEntityManager;

    protected ComponentMapper<Physics> mPhysics;
    protected ComponentMapper<Gravity> mGravity;

    /**
     * Creates a new EntityProcessingSystem.
     */
    public GravitySystem() {
        this.setPassive(true);
    }

    @Override
    protected void processSystem() {
        Array<Chunk> chunks = chunkManager.getLoadedChunks();
        for (Chunk chunk : chunks) {
            for (UUID eUUID : chunk.entities) {
                Entity e = uuidEntityManager.getEntity(eUUID);
                if (e != null) {
                    Physics physics = mPhysics.get(e);
                    if (physics != null) {
                        float gX, gY;
                        //If the entity has a custom gravity assigned
                        if (mGravity.has(e)) {
                            Gravity gravity = mGravity.get(e);
                            gX = gravity.gX;
                            gY = gravity.gY;
                        } else {
                            gX = chunk.gravity.x;
                            gY = chunk.gravity.y;
                        }

                        physics.body.applyForceToCenter(gX * physics.body.getMass(), gY * physics.body.getMass(), false);
                    }
                }
            }
        }
    }
}
