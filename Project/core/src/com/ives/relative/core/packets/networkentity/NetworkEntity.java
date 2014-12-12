package com.ives.relative.core.packets.networkentity;


import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.ives.relative.entities.components.body.PhysicsPosition;
import com.ives.relative.entities.components.client.VisualComponent;
import com.ives.relative.entities.components.network.NetworkBodyComponent;
import com.ives.relative.entities.components.network.NetworkVisualComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public List<Component> components;

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
    private void handleComponents(Entity entity) {
        components = new ArrayList<Component>();
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
     * Add the component to the list, filters out special components which cannot be sent over the network and creates
     * a networkComponent for it.
     * @param component The component which needs to be added or converted.
     */
    private void addComponent(Component component) {
        if (component instanceof PhysicsPosition) {
            components.add(new NetworkBodyComponent((PhysicsPosition) component));
        } else if(component instanceof VisualComponent) {
            //Checks if there is a texture assigned.
            if(textureFile != null) {
                if (!textureFile.equals("")) {
                    components.add(new NetworkVisualComponent(textureFile, ((VisualComponent) component).width, ((VisualComponent) component).height));
                    return;
                }
            }

            components.add(new NetworkVisualComponent((VisualComponent) component));
        } else {
            components.add(component);
        }
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
        //entity.add(factory.createVisual());
        return entity;
    }
}
