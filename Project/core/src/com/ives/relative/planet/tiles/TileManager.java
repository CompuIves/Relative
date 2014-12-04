package com.ives.relative.planet.tiles;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.*;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.HashMap;

/**
 * Created by Ives on 12/1/2014.
 */
public class TileManager {
    public HashMap<String, SolidTile> solidTiles;
    GameManager game;

    public TileManager(GameManager game) {
        solidTiles = new HashMap<String, SolidTile>();
        this.game = game;

        //This is TEMPORARY
        solidTiles.put("dirt", new SolidTile().setDurability(1).setId("dirt").setTexture(new Texture("dirt.png")).setAffectGravity(true));
        solidTiles.put("bedrock", new SolidTile().setDurability(123).setId("bedrock").setTexture(new Texture("bedrock.png")));
    }

    public static PolygonShape getCube(float width, float height) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width / 2f, height / 2f);
        return polygonShape;
    }

    /**
     * Creates an entity with PositionComponent, TileComponent and BodyComponent
     * @param world the world the tile is in
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param tileID the tileID
     * @return the new entity created
     */
    public Entity createTile(World world, int x, int y, int z, String tileID, boolean gravity) {
        Entity e = new Entity();
        e.add(new PositionComponent(world, x, y, z));

        SolidTile tile = solidTiles.get(tileID);
        e.add(new TileComponent(tile));

        e.add(new HealthComponent(tile.getDurability()));

        e.add(new VisualComponent(tile.getTexture(), tile.getWidth(), tile.getHeight()));

        BodyDef bodyDef = new BodyDef();
        if(tile.isAffectGravity() && gravity)
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        else
            bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        PolygonShape shape = getCube(tile.getWidth(), tile.getHeight());
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.9f;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        e.add(new BodyComponent(body));

        game.engine.addEntity(e);

        return e;
    }
}
