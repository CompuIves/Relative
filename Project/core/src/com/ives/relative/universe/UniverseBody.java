package com.ives.relative.universe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.CollisionManager;
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
    public final int depth;
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
    private final Array<Body> bodiesToRemove;
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
    protected int vx, vy;
    private Body body;
    private Matrix3 mScale;
    private Matrix3 mTranslation;
    private Matrix3 mRotation;

    public UniverseBody(CollisionManager collisionManager, String id, UniverseBody parent, int x, int y, int width, int height, float rotation, Vector2 scale, int chunkSize) {
        this.id = id;
        this.parent = parent;

        if (parent != null) {
            this.depth = parent.depth + 1;
        } else {
            this.depth = 0;
        }

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
        world.setContactListener(collisionManager);
        createEdgeLines();
        chunks = new HashMap<Vector2, Chunk>();
        this.chunkSize = chunkSize;

        this.chunkBuilder = new EmptyChunk(this, null, null);

        bodiesToRemove = new Array<Body>(10);
    }

    public void setChunkBuilder(ChunkBuilder chunkBuilder) {
        this.chunkBuilder = chunkBuilder;
    }

    /**
     * Gets called every {@link com.ives.relative.universe.UniverseSystem#ITERATIONS}
     */
    protected void update() {
        for (Body body : bodiesToRemove) {
            world.destroyBody(body);
        }
        bodiesToRemove.clear();
        world.step(UniverseSystem.ITERATIONS, 6, 6);
        for (UniverseBody universeBody : children) {
            universeBody.update();
        }

    }

    public UniverseBody getParent() {
        return parent;
    }

    /**
     * Creates a body around the universebody for collision detection
     */
    private void createEdgeLines() {
        BodyDef bodyDefEdge = new BodyDef();
        Body edge = world.createBody(bodyDefEdge);
        edge.setUserData(parent); //Parent is bound to this body, since when you enter that place you also enter the parent

        FixtureDef fixtureDef = new FixtureDef();
        ChainShape shapeEdge = new ChainShape();

        float[] vertexes = new float[]{-width / 2, height / 2, //Top left
                width / 2, height / 2, //Top right
                width / 2, -height / 2, //Bottom right
                -width / 2, -height / 2}; //Bottom left
        shapeEdge.createLoop(vertexes);

        fixtureDef.shape = shapeEdge;
        fixtureDef.isSensor = true;
        edge.createFixture(fixtureDef).setUserData("edge");

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

    public UniverseBody getChild(String id) {
        for (UniverseBody universeBody : children) {
            if (universeBody.id.equals(id))
                return universeBody;
        }
        return null;
    }

    public UniverseBody getTopUniverseBody(Vector2 pos, boolean createVectorCopy) {
        return getTopUniverseBody(new Vector3(pos, 0), createVectorCopy);
    }

    /**
     * Finds the highest universebody on the map (children are higher), also transforms the vector to the universebody (if boolean is set to false)
     * @param pos A vector with three values: x, y, and rotation
     * @param createVectorCopy if false the vector will be transformed to the coordinatesystem of returning universebody
     * @return universebody at pos
     */
    public UniverseBody getTopUniverseBody(Vector3 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector3 rPos = createVectorCopy ? pos.cpy() : pos;
        Vector2 xyPos = new Vector2(rPos.x, rPos.y);

        UniverseBody u = this;

        //If outside, get parent first.
        if (!isInBody(xyPos)) {
            u = getHighestParent(rPos, createVectorCopy);
        }

        return u.getBottomChild(rPos, createVectorCopy);
    }

    /**
     * Gets the lowest child at that pos (so no parents)
     *
     * @param pos A vector with three values: x, y, and rotation
     * @param createVectorCopy if false the vector will be transformed to the coordinatesystem of the child
     * @return lowest child
     */
    public UniverseBody getBottomChild(Vector3 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector3 rPos = createVectorCopy ? pos.cpy() : pos;
        Vector2 xyPos = new Vector2(rPos.x, rPos.y);

        UniverseBody universeBody = getChild(xyPos);
        if (universeBody != null) {
            universeBody.inverseTransformVector(rPos);
            return universeBody.getBottomChild(rPos, createVectorCopy);
        } else {
            return this;
        }
    }

    /**
     * Gets the highest parent at this point
     * @param pos A Vector with three values; x, y and rotation.
     * @param createVectorCopy If this is set to false the vector3 will be altered to the coordinatesystem of the returning parent
     * @return body
     */
    public UniverseBody getHighestParent(Vector3 pos, boolean createVectorCopy) {
        //Prevent changing the original position vector
        Vector3 rPos = createVectorCopy ? pos.cpy() : pos;
        Vector2 xyPos = new Vector2(rPos.x, rPos.y);
        if (!isInBody(xyPos)) {
            UniverseBody universeBody = parent;
            transformVector(rPos);
            return universeBody.getHighestParent(rPos, createVectorCopy);
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
     * Returns false is there is a child at pos, returns false if out of body, return true of the point is truly in body
     *
     * @param pos
     * @return
     */
    public boolean isAtPoint(Vector2 pos) {
        return getChild(pos) == null && isInBody(pos);
    }

    public void transformVectorToUniverseBody(UniverseBody universeBody, Vector2 vec) {
        transformVectorToUniverseBody(universeBody, new Vector3(vec, 0));
    }

    public void transformVectorToUniverseBody(UniverseBody universeBody, Vector3 vec) {
        if (this.depth > universeBody.depth) {
            transformVector(vec);
            UniverseBody uBod = parent;
            uBod.transformVectorToUniverseBody(universeBody, vec);
        } else if (this.depth < universeBody.depth) {
            inverseTransformVector(vec);
            UniverseBody uBod = getChild(new Vector2(vec.x, vec.y));
            //TODO find nicer way to fix this
            if (uBod == null) {
                uBod = getChild(universeBody.id);
            }
            uBod.transformVectorToUniverseBody(universeBody, vec);
        }
    }

    /**
     * Transforms a vector from the local coordinate system to the coordinate system of the parent
     * @param vector
     */
    public void transformVector(Vector3 vector) {
        Vector2 v2 = new Vector2(vector.x, vector.y);
        transformVector(v2);
        vector.set(v2.x, v2.y, vector.z + rotation);
    }

    /**
     * Transforms a vector from the local coordinate system to the coordinate system of the parent
     *
     * @param pos
     */
    public void transformVector(Vector2 pos) {
        pos.mul(mTransform);
    }

    /**
     * Transforms a vector from the coordinate system of a parent to the local coordinate system
     * @param vector vector with x,y,rotation (degrees)
     */
    public void inverseTransformVector(Vector3 vector) {
        Vector2 v2 = new Vector2(vector.x, vector.y);
        inverseTransformVector(v2);
        vector.set(v2.x, v2.y, vector.z - rotation);
    }

    /**
     * Transforms a vector from the coordinate system of a parent to the local coordinate system
     *
     * @param pos
     */
    public void inverseTransformVector(Vector2 pos) {
        pos.mul(mInverseTransform);
    }

    /**
     * Sets the transformation matrices according to the positions it has.
     */
    void setTransform() {
        //Set matrices
        mTranslation.setToTranslation(x, y);
        mRotation.setToRotation(rotation);
        mScale.setToScaling(scale);

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
