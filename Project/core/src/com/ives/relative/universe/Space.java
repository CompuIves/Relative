package com.ives.relative.universe;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.CollisionManager;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.builders.ChunkBuilder;
import com.ives.relative.universe.chunks.builders.EmptyChunk;

import java.util.HashMap;

/**
 * Created by Ives on 18/1/2015.
 * <p>
 * All objects having this contain a position and a transform in the universe. It has a width, height, rotation and coordinates (which are
 * aligned to the middle). Every Space contains its own world, this means that each body has an own Box2D world and their own chunks. This
 * opens up the possibility to rotate and move the Space independently from its parent. UniverseBodies have children and parents (like
 * a solar system has planets).
 * </p>
 */
public class Space {
    public final int depth;
    public final String id;

    public final HashMap<Vector2, Chunk> chunks;
    public final int chunkSize;
    public final int width, height;

    public final World world;
    protected final Space parent;
    private final Array<Body> bodiesToRemove;
    public String name;
    /**
     * Determines how chunks should be generated in this Space, for example on planets there exists a
     * SquarePlanet
     */
    public ChunkBuilder chunkBuilder;

    public final boolean infinite;

    public Space(String id, Space parent, int width, int height, int chunkSize, Vector2 gravity) {
        this.id = id;
        this.parent = parent;

        this.width = width;
        this.height = height;

        world = new World(gravity, true);
        world.setContactListener(CollisionManager.getInstance());
        chunks = new HashMap<Vector2, Chunk>();
        this.chunkSize = chunkSize;

        this.chunkBuilder = new EmptyChunk(this, null, null);
        infinite = true;

        if (parent != null) {
            this.depth = parent.depth + 1;
        } else {
            this.depth = 0;
        }

        bodiesToRemove = new Array<Body>(10);
    }

    public void setChunkBuilder(ChunkBuilder chunkBuilder) {
        this.chunkBuilder = chunkBuilder;
    }

    /**
     * Gets called every {@link UniverseSystem#ITERATIONS}
     */
    protected void update() {
        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();

        world.step(UniverseSystem.ITERATIONS, 6, 6);
    }

    /**
     * This method removes the body from the world while the world isn't stepping.<br> <b>USE THIS METHOD
     * ONLY TO REMOVE BODIES</b></br>.
     */
    public void removeBody(Body body) {
        bodiesToRemove.add(body);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Space that = (Space) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Space with id: " + id + " with width: " + width + ", height: " + height;
    }
}
