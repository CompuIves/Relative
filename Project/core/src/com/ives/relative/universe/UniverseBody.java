package com.ives.relative.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
 * All objects having this contain a position and a transform in the universe. It has a width, height, rotation and coordinates (which are
 * aligned to the middle). Every UserBody contains its own world, this means that each body has an own Box2D world and their own chunks. This
 * opens up the possibility to rotate and move the UniverseBody independently from its parent. UniverseBodies have children and parents (like
 * a solar system has planets).
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
    public final int width, height;
    public final World world;
    protected final Vector2 scale;
    protected final UniverseBody parent;
    protected final Array<UniverseBody> children;
    private final Array<Body> childrenBodies;
    public String name;
    /**
     * Determines how chunks should be generated in this UniverseBody, for example on planets there exists a
     * SquarePlanet
     */
    public ChunkBuilder chunkBuilder;
    /**
     * rotation in degrees
     */
    protected float rotation;
    protected int x, y;
    private Body body;
    private Matrix3 mScale;
    private Matrix3 mTranslation;
    private Matrix3 mRotation;

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
     * Gets called every {@link com.ives.relative.universe.UniverseSystem#ITERATIONS}
     */
    protected void update() {
        for (UniverseBody universeBody : children) {
            universeBody.update();
        }
        world.step(UniverseSystem.ITERATIONS, 10, 10);
    }

    public UniverseBody getParent() {
        return parent;
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
     * Finds the lowest UniverseBody at given position
     * @param pos
     * @param createVectorCopy should the vector given be transformed to the coordinatesystem of the child given?
     * @return child
     */
    public UniverseBody getBottomUniverseBody(Vector2 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector2 rPos = createVectorCopy ? pos.cpy() : pos;

        //If outside, use parent.
        if (!isInBody(rPos)) {
            transformVector(rPos);
            return parent;
        }

        return getBottomChild(pos, createVectorCopy);
    }

    /**
     * Gets the lowest child at that pos (so no parents)
     *
     * @param pos
     * @param createVectorCopy
     * @return lowest child
     */
    public UniverseBody getBottomChild(Vector2 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector2 rPos = createVectorCopy ? pos.cpy() : pos;

        UniverseBody universeBody = getChild(rPos);
        if (universeBody != null) {
            universeBody.inverseTransformVector(rPos);
            return universeBody.getBottomChild(rPos, createVectorCopy);
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
        bodyDef.angle = universeBody.rotation * MathUtils.degreesToRadians;

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(universeBody.width / 2, universeBody.height / 2);
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.setUserData(universeBody);
        body.createFixture(fixtureDef);

        universeBody.body = body;
        return body;
    }

    /**
     * Checks if the specified position is in this universebody
     *
     * @param pos position in transformation of universebody
     * @return
     */
    public boolean isInBody(Vector2 pos) {
        return RelativeMath.isInBounds(pos.x, -width / 2, width / 2)
                && RelativeMath.isInBounds(pos.y, -height / 2, height / 2);
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
     * Recursively transforms the vector until it is the transformation to the lowest child.
     * For example I have an object in a starsystem and I want it to be converted to the position of a planet.
     *
     * @param uBod child to convert it to
     * @param vec position to be transformed
     *
     * @return Vector3 with (x, y, rotation (degrees))
     */
    public Vector3 transformPositionParentToChild(UniverseBody uBod, Vector2 vec) {
        Vector2 pos = vec.cpy();
        float rotation = 0;
        while (uBod != this && uBod != null) {
            rotation += uBod.rotation;
            uBod.inverseTransformVector(pos);
            uBod = uBod.getChild(pos);
        }
        return new Vector3(pos.x, pos.y, rotation);
    }

    /**
     * Sets the transformation matrices according to the positions it has.
     */
    void setTransform() {
        //Set matrices
        mTranslation.setToTranslation(x, y);
        mRotation.setToRotation(rotation);
        mScale.setToScaling(scale);

        //transpose mRotation again to get it to the old state
        mRotation.transpose();

        mTransform.idt().mul(mTranslation).mul(mRotation).mul(mScale);
        mInverseTransform.idt().mul(mTransform).inv();
    }

    protected void updateBody() {
        body.setTransform(x, y, rotation * MathUtils.degreesToRadians);
    }

    public float getRotation() {
        return rotation;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public boolean hasChildren() {
        return children.size != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniverseBody that = (UniverseBody) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "UniverseBody with id: " + id + " at x: " + x + ", y: " + y + ", with width: " + width + ", height: " + height;
    }
}
