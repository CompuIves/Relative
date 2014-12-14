package com.ives.relative.network.networkentity;


import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.TileC;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.components.planet.WorldC;
import com.ives.relative.entities.factories.Player;
import com.ives.relative.entities.factories.Tile;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.PlanetManager;
import com.ives.relative.managers.SolidTile;
import com.ives.relative.managers.TileManager;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public Array<Component> components;

    public long id;

    public NetworkEntity() {
    }

    /**
     * If the other endpoint already has the needed texture this will be used
     *
     * @param entity The entity
     */
    public NetworkEntity(Entity entity, long id) {
        handleComponents(entity);
        this.id = id;
    }

    /**
     * Creates a componentlist and calls a method to add them
     *
     * @param entity The entity with the components
     */
    private void handleComponents(Entity entity) {
        components = new Array<Component>();
        Bag<Component> tempComponents = new Bag<Component>();
        entity.getComponents(tempComponents);
        for (Component component : tempComponents) {
            components.add(component);
        }
        /*
        components.ensureCapacity(components.getCapacity());
        for (Component component : components) {
            addComponent(component);
        }
        */
    }

    private void finishEntity(Entity entity, Type type) {
        Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
        Position position = entity.getWorld().getMapper(Position.class).get(entity);
        Visual visual = entity.getWorld().getMapper(Visual.class).get(entity);
        switch (type) {
            case PLAYER:
                physics = entity.getWorld().getMapper(Physics.class).get(entity);
                position = entity.getWorld().getMapper(Position.class).get(entity);
                Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);
                Entity planet = entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID);

                physics.body = Player.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, planet);
                visual.texture = new TextureRegion(new Texture("player.png"));
                break;
            case TILE:
                TileC tileC = entity.getWorld().getMapper(TileC.class).get(entity);
                SolidTile tile = entity.getWorld().getManager(TileManager.class).solidTiles.get(tileC.id);
                com.badlogic.gdx.physics.box2d.World world = entity.getWorld().getMapper(WorldC.class).get(entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID)).world;
                physics.body = Tile.createBody(entity, tile, position.x, position.y, true, world);
                visual.texture = tile.textureRegion;
                break;
            case PLANET:
                Name name = entity.getWorld().getMapper(Name.class).get(entity);
                entity.getWorld().getManager(PlanetManager.class).addPlanet(name.internalName, entity);
                break;
            case OTHER:
                break;
            default:
                break;
        }
    }

    /**
     * This adds all the components in the entity
     *
     * @return The created entity
     */
    public Entity createEntity(World world) {
        Entity entity = world.createEntity();
        entity = createEntity(world, entity);
        return entity;
    }

    public Entity createEntity(World world, Entity entity) {
        for (Component component : components) {
            entity.edit().add(component);
        }

        if (id != 0)
            world.getManager(NetworkManager.class).updateEntity(id, entity);

        NetworkC networkC = world.getMapper(NetworkC.class).get(entity);

        if (networkC.type == null) {
            networkC.type = Type.OTHER;
        }

        System.out.println("TRYING TO CREATE FUCKING ENTITY WITH ID: " + id);

        finishEntity(entity, networkC.type);
        //entity.add(factory.createVisual());

        System.out.println("Created entity with ID: " + id + " and TYPE: " + networkC.type);
        return entity;
    }

    public enum Type {
        PLAYER,
        TILE,
        PLANET, OTHER
    }
}
