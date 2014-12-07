package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.*;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 5/12/2014.
 */
public class TileFactory {

    /**
     * Creates an entity with PositionComponent, TileComponent and BodyComponent
     * @param world the world the tile is in
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param tile the tile
     * @return the new entity created
     */
    public static Entity createTile(World world, float x, float y, int z, SolidTile tile, boolean gravity) {
        Entity e = new Entity();
        e.add(new PositionComponent(world, x, y, z));
        e.add(new NameComponent(tile.getId(), tile.getName()));
        e.add(new TileComponent(tile));
        e.add(new DurabilityComponent(tile.getDurability()));
        e.add(new VisualComponent(tile.getTextureRegion(), tile.getWidth(), tile.getHeight()));

        BodyDef bodyDef = new BodyDef();
        if(tile.isGravity() && gravity)
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        else
            bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        PolygonShape shape = TileManager.getCube(tile.getWidth(), tile.getHeight());
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

        return e;
    }
}
