package com.ives.relative.tiles;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by Ives on 12/1/2014.
 */
public class Tile {
    Body body;
    int width, height;
    int x, y;
    World world;

    public Tile(World world, int x, int y) {
        this.width = 1;
        this.height = 1;
        this.x = x;
        this.y = y;
        this.world = world;
        this.body = createBody(width, height, x, y);
    }

    public Tile(int width, int height, int x, int y, World world) {
        this.width = width;
        this.height = height;
        this.world = world;
        this.body = createBody(width, height, x, y);
    }

    public Tile(int width, int height, Body body) {
        this.width = width;
        this.height = height;
        this.body = body;
    }

    private Body createBody(int width, int height, int x, int y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(TileManager.getX(x), TileManager.getY(y));

        body = world.createBody(bodyDef);
        PolygonShape shape = TileManager.getCube(width, height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.6f;
        fixtureDef.friction = 0.4f;

        Fixture fixture = body.createFixture(fixtureDef);

        shape.dispose();

        System.out.println("Created body at " + x + " " + y);

        return body;
    }
}
