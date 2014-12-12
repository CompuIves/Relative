package com.ives.relative.core.packets.networkentity;


import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public Array<Component> components;

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
     * @param entity entity to be converted
     */
    public NetworkEntity(Entity entity) {
        handleComponents(entity);
    }

    /**
     * If the other endpoint already has the needed texture this will be used
     * @param entity The entity
     * @param textureFile The location of the texturefile
     */
    public NetworkEntity(Entity entity, String textureFile) {
        this.textureFile = textureFile;
        handleComponents(entity);
    }

    /**
     * Creates a componentlist and calls a method to add them
     * @param entity The entity with the components
     */
    private void handleComponents(final Entity entity) {
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

    /**
     * This adds all the components in the entity
     * @return The entity created
     */
    public Entity createEntity(World world) {
        Entity entity = world.createEntity();
        for (Component component : components) {
            entity.edit().add(component);
        }
        world.getEntityManager().added(entity);
        //entity.add(factory.createVisual());
        return entity;
    }
}
