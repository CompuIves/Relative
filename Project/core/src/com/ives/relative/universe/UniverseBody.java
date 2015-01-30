package com.ives.relative.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.Chunk;
import com.ives.relative.universe.chunks.builders.ChunkBuilder;
import com.ives.relative.universe.chunks.builders.EmptyChunk;
import com.ives.relative.utils.RelativeMath;

import java.util.HashMap;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * All objects having this contain a position in the universe. It has a width and a height and coordinates (which are
 * aligned to the middle).
 */
public class UniverseBody {
    public String name;
    public final String id;

    /**
     * Transformation matrix relative to the parent it is in.
     */
    public final Matrix3 mTransform;
    /**
     * Inversed transformation matrix relative to parent
     */
    public final Matrix3 mInverseTransform;
    private Matrix3 mScale;
    private Matrix3 mTranslation;
    private Matrix3 mRotation;
    private Matrix3 mInverseScale;
    private Matrix3 mInverseRotation;
    private Matrix3 mInverseTranslation;

    /**
     * Determines how chunks should be generated in this UniverseBody, for example on planets there exists a
     * SquarePlanet
     */
    public ChunkBuilder chunkBuilder;
    public final HashMap<Vector2, Chunk> chunks;
    public final int chunkSize;

    protected int x, y;
    protected final int width, height;
    /** rotation in radians */
    protected final float rotation;
    protected final Vector2 scale;

    protected final UniverseBody parent;
    protected final Array<UniverseBody> children;
    private final Array<Body> childrenBodies;

    private final World world;

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
        childrenBodies = new Array<Body>();

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
     * Gets child at specified position
     * @param pos
     * @return true/false
     */
    public UniverseBody getChild(Vector2 pos) {
        //Check if another body is in there
        for(Body body : childrenBodies) {
            if(body.getFixtureList().first().testPoint(pos))
                return (UniverseBody) body.getUserData();
        }
        return null;
    }

    /**
     * Finds the lowest child at the given position
     * @param pos
     * @param createVectorCopy should the vector given be transformed to the coordinatesystem of the child given?
     * @return child
     */
    public UniverseBody getLowestChild(Vector2 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector2 rPos = createVectorCopy ? pos.cpy() : pos;
        UniverseBody universeBody = getChild(rPos);
        if(universeBody != null) {
            universeBody.inverseTransformVector(rPos);
            return universeBody.getLowestChild(rPos, createVectorCopy);
        } else {
            return this;
        }
    }

    /**
     * Adds a child to this universebody, this UniverseBody also adds a Box2D sensor to its own world for collision
     * detection with the body.
     *
     * @param universeBody
     * @return the child given for chaining
     */
    public UniverseBody addChild(UniverseBody universeBody) {
        Vector2 min = new Vector2(universeBody.x - universeBody.width / 2, universeBody.y - universeBody.height / 2);
        Vector2 max = new Vector2(universeBody.x + universeBody.width / 2, universeBody.y + universeBody.height / 2);

        if (isInBody(min) && isInBody(max)) {
            children.add(universeBody);
            childrenBodies.add(createChildBody(universeBody));
            return universeBody;
        } else {
            Gdx.app.error("UniverseCreator", "Couldn't add " + universeBody.toString() + " + to " + this.toString());
            return null;
        }
    }

    private Body createChildBody(UniverseBody universeBody) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.allowSleep = true;
        bodyDef.position.set(universeBody.x, universeBody.y);
        bodyDef.angle = universeBody.rotation;

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(universeBody.width / 2, universeBody.height / 2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.setUserData(universeBody);
        body.createFixture(fixtureDef);

        return body;
    }

    /**
     * Checks if the specified position is in this universebody
     *
     * @param pos position which should be transformed
     * @return
     */
    public boolean isInBody(Vector2 pos) {
        return RelativeMath.isInBounds(pos.x, this.x - width / 2, this.x + width / 2)
                && RelativeMath.isInBounds(pos.y, this.y - height / 2, this.y + height / 2);
    }

    /**
     * Transforms a vector from the local coordinate system to the coordinate system of the parent
     * @param vector
     */
    public void transformVector(Vector2 vector) {
        vector.mul(mTransform);
    }

    /**
     * Transforms a vector from the coordinate system of a parent to the local coordinate system
     * @param vector
     */
    public void inverseTransformVector(Vector2 vector) {
        vector.mul(mInverseTransform);
    }

    /**
     * Sets the transformation matrices according to the positions it has.
     */
    void setTransform() {
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

        mTransform.idt().mul(mTranslation).mul(mRotation).mul(mScale);
        mInverseTransform.idt().mul(mScale).mul(mRotation).mul(mTranslation);
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
        return "UniverseBody with id: " + id + " at x: " + x + ", y: " + y + ", with width: " + width + ", height: " + height;
    }
}
