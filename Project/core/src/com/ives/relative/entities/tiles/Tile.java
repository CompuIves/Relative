package com.ives.relative.entities.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 2/12/2014.
 */
public class Tile {
    SolidTile baseTile;
    TilePos tilePos;
    int health;
    Body body;

    public Tile(SolidTile baseTile, TilePos tilePos) {
        this.baseTile = baseTile;
        this.health = 100;
        this.tilePos = tilePos;
        this.body = createBody(baseTile.width, baseTile.height);
    }

    public int hitTile(int damage) {
        return this.health -= damage * baseTile.getDurability();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(baseTile.getTexture(), tilePos.x, tilePos.y - 0.5f, 1, 1);
    }

    private Body createBody(int width, int height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(tilePos.x, tilePos.y);

        body = tilePos.planet.getWorld().createBody(bodyDef);
        PolygonShape shape = TileManager.getCube(width, height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.6f;
        fixtureDef.friction = 0.4f;

        Fixture fixture = body.createFixture(fixtureDef);

        body.setUserData(this);
        fixture.setUserData(this);
        shape.dispose();

        System.out.println("Created body at " + tilePos.x + " " + tilePos.y);

        return body;
    }
}
