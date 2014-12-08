package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.ives.relative.entities.components.*;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.planet.tiles.TileManager;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

/**
 * Created by Ives on 5/12/2014.
 */
public class TileFactory extends Factory {

    /**
     * Creates an entity with TileComponent and BodyComponent
     * @param planet the planet the tile is in
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param tile the tile
     * @return the new entity created
     */
    public Entity createTile(Entity planet, float x, float y, int z, SolidTile tile, boolean gravity) {
        Entity e = new Entity();
        World world = Mappers.world.get(planet).world;
        String worldID = Mappers.name.get(planet).internalName;
        e.add(new NameComponent(tile.id, tile.name));
        e.add(new TileComponent(tile));
        e.add(new DurabilityComponent(tile.durability));
        e.add(new VisualComponent(tile.textureRegion, tile.width, tile.height));

        BodyDef bodyDef = new BodyDef();
        if(tile.gravity && gravity)
            bodyDef.type = BodyDef.BodyType.DynamicBody;
        else
            bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        PolygonShape shape = TileManager.getCube(tile.width, tile.height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.0f;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.6f;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(e);
        body.setUserData(e);
        shape.dispose();
        e.add(new BodyComponent(body, z, worldID));

        return e;
    }
}
