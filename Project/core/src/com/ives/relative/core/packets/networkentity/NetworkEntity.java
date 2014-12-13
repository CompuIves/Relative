package com.ives.relative.core.packets.networkentity;


import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.factories.Player;
import com.ives.relative.managers.PlanetManager;

import java.util.UUID;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public Array<Component> components;
    public int entityID;
    public Type type;
    /**
     * If the texture is on the other side this string will be set to the location.
     * The NetworkEntity will add a VisualComponent with a visual when this is null.
     * Otherwise it will just load the texture when arrived by loading it using this String.
     */
    public String textureFile;

    public NetworkEntity() {
    }

    /**
     * Create a networked entity
     *
     * @param entity entity to be converted
     */
    public NetworkEntity(Entity entity) {
        handleComponents(entity);
        type = Type.OTHER;
    }

    /**
     * If the other endpoint already has the needed texture this will be used
     * @param entity The entity
     * @param type if it is a type there wouldn't be that much information needed
     */
    public NetworkEntity(Entity entity, Type type) {
        this.type = type;
        handleComponents(entity);
    }

    /**
     * Creates a componentlist and calls a method to add them
     * @param entity The entity with the components
     */
    private void handleComponents(final Entity entity) {
        this.entityID = entity.getId();
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

    private void finishEntity(Entity entity) {
        switch (type) {
            case PLAYER:
                Physics physics = entity.getWorld().getMapper(Physics.class).get(entity);
                Position position = entity.getWorld().getMapper(Position.class).get(entity);
                Velocity velocity = entity.getWorld().getMapper(Velocity.class).get(entity);

                physics.body = Player.createBody(entity, position.x, position.y, velocity.vx, velocity.vy, entity.getWorld().getManager(PlanetManager.class).getPlanet(position.worldID));

                Visual visual = entity.getWorld().getMapper(Visual.class).get(entity);
                visual.texture = new TextureRegion(new Texture("player.png"));
                break;

        }
    }

    /**
     * This adds all the components in the entity
     * @return The created entity
     */
    public Entity createEntity(World world) {
        Entity entity = world.createEntity();
        entity.setUuid(new UUID(entityID, entityID));
        return createEntity(world, entity);
    }

    public Entity createEntity(World world, Entity entity) {
        for (Component component : components) {
            entity.edit().add(component);
        }
        finishEntity(entity);

        //Force add the entity because we can't wait for the world next iteration.
        world.getEntityManager().added(entity);
        //entity.add(factory.createVisual());
        return entity;
    }

    public enum Type {
        PLAYER,
        TILE,
        OTHER
    }
}
