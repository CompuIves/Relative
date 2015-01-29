package com.ives.relative.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.builders.ChunkBuilder;
import com.ives.relative.universe.chunks.builders.EmptyChunk;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * All objects having this contain a position in the universe. It has a width and a height and coordinates (which are
 * aligned to the middle).
 */
public class UniverseBody {
    public final String id;
    /**
     * Transformation matrix relative to the parent it is in.
     */
    public final Matrix3 mTransform;
    /**
     * Inversed transformation matrix relative to parent
     */
    public final Matrix3 mInverseTransform;
    public final HashMap<Vector2, Chunk> chunks;
    public final int chunkSize;
    protected final int width, height;
    protected final float rotation;
    protected final Vector2 scale;
    protected final UniverseBody parent;
    protected final Array<UniverseBody> children;
    private final World world;
    public String name;
    /**
     * Determines how chunks should be generated in this UniverseBody, for example on planets there exists a
     * SquarePlanet
     */
    public ChunkBuilder chunkBuilder;
    protected int x, y;
    private float aabbWidth, aabbHeight;
    private Matrix3 mScale;
    private Matrix3 mTranslation;
    private Matrix3 mRotation;
    private Matrix3 mInverseScale;
    private Matrix3 mInverseRotation;
    private Matrix3 mInverseTranslation;

    public UniverseBody(String id, UniverseBody parent, int x, int y, int width, int height, float rotation, Vector2 scale) {
        this.id = id;
        this.parent = parent;

        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
        this.width = width;
        this.height = height;

        this.children = new Array<UniverseBody>();

        mTransform = new Matrix3();
        mInverseTransform = new Matrix3();
        mScale = new Matrix3();
        mRotation = new Matrix3();
        mTranslation = new Matrix3();
        mInverseScale = new Matrix3();
        mInverseRotation = new Matrix3();
        mInverseTranslation = new Matrix3();
        setTransform();

        world = new World(new Vector2(0, 0), true);
        chunks = new HashMap<Vector2, Chunk>();
        chunkSize = 16;

        this.chunkBuilder = new EmptyChunk(this, null, null);
    }

    public void setChunkBuilder(ChunkBuilder chunkBuilder) {
        this.chunkBuilder = chunkBuilder;
    }

    /**
     * Gets the lowest child in the UniverseBody hierarchy, lowest possibility is planet.
     *
     * @param x x pos
     * @param y y pos
     * @return
     */
    public UniverseBody getChild(float x, float y) {
        Iterator<UniverseBody> it = children.iterator();
        UniverseBody lowestUniverseBody = null;

        while (it.hasNext()) {
            UniverseBody universeBody = it.next();
            if (universeBody.isInBody(new Vector2(x, y))) {
                //starts the loop from the beginning with the new UniverseBody
                lowestUniverseBody = universeBody;
                it = universeBody.children.iterator();
            }
        }

        return lowestUniverseBody;
    }

    /**
     * Adds a child to this universebody
     *
     * @param universeBody
     * @return the child given for chaining
     */
    public UniverseBody addChild(UniverseBody universeBody) {
        Vector2 min = new Vector2(universeBody.x - universeBody.width / 2, universeBody.y - universeBody.height / 2);
        Vector2 max = new Vector2(universeBody.x + universeBody.width / 2, universeBody.y + universeBody.height / 2);

        if (isInBody(min) &&
                isInBody(max)) {
            children.add(universeBody);
            return universeBody;
        } else {
            Gdx.app.error("UniverseCreator", "Couldn't add " + universeBody.toString() + " + to " + this.toString());
            return null;
        }
    }

    /**
     * Creates a chunk at specified indexes
     * @param x index of first chunk
     * @param y index of second chunk
     * @return
     */
    public Chunk createChunk(int x, int y) {
        Vector2 chunkLoc = new Vector2(x, y);
        if (isInBody(chunkLoc)) {
            Chunk chunk;
            chunk = chunkBuilder.buildChunk(x, y);
            chunks.put(new Vector2(x, y), chunk);
            return chunk;
        } else
            return null;
    }

    /**
     * Checks if the specified position is in this universebody
     *
     * @param pos position which should be transformed
     * @return
     */
    public boolean isInBody(Vector2 pos) {
        pos.mul(mInverseTransform);
        if (isInRange(pos)) {
            return RelativeMath.isInBounds(pos.x, this.x - width / 2, this.x + width / 2)
                    && RelativeMath.isInBounds(pos.y, this.y - height / 2, this.y + height / 2);
        }

        return false;

    }

    /**
     * Checks if the specified position is in the AABB of the universebody
     *
     * @param pos
     * @return
     */
    private boolean isInRange(Vector2 pos) {
        return RelativeMath.isInBounds(pos.x, this.x - aabbWidth / 2, this.x + aabbWidth / 2)
                && RelativeMath.isInBounds(pos.y, this.y - aabbHeight / 2, this.y + aabbHeight / 2);
    }

    /**
     * Sets the transformation matrices according to the positions it has.
     */
    private void setTransform() {
        //Set matrices
        mTranslation.setToTranslation(x, y);
        mRotation.setToRotation(rotation);
        mScale.setToScaling(scale);

        //Set inversion matrices
        mInverseTranslation.setToTranslation(-x, -y);
        mInverseScale.setToScaling(1 / scale.x, 1 / scale.y);
        mInverseRotation.set(mRotation.transpose());

        //transpose mRotation again to get it to the old state
        mRotation.transpose();

        mTransform.set(mTranslation.mul(mRotation).mul(mScale));
        mInverseTransform.set(mInverseScale.mul(mInverseRotation).mul(mInverseTranslation));

        aabbWidth = (float) (width * Math.cos(rotation) - height * Math.sin(rotation));
        aabbHeight = (float) (width * Math.sin(rotation) + height*Math.cos(rotation));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniverseBody that = (UniverseBody) o;

        if (height != that.height) return false;
        if (width != that.width) return false;
        if (x != that.x) return false;
        if (y != that.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "UniverseBody at x: " + x + ", y: " + ", with width: " + width + ", height: " + height;
    }
}
