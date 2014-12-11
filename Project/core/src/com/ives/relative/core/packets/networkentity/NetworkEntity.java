package com.ives.relative.core.packets.networkentity;


import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.body.PhysicsPosition;
import com.ives.relative.entities.components.network.NetworkBodyComponent;
import com.ives.relative.entities.components.network.NetworkVisualComponent;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public Bag<Component> componentsBag;

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
        componentsBag = new Bag<Component>();

        for (Component component : entity.getComponents(componentsBag)) {
            addComponent(component);
        }
    }

    /**
     * Add the component to the list, filters out special components which cannot be sent over the network and creates
     * a networkComponent for it.
     * @param component The component which needs to be added or converted.
     */
    private void addComponent(Component component) {
        if (component instanceof PhysicsPosition) {
            componentsBag.add(new NetworkBodyComponent((PhysicsPosition) component));
        } else if(component instanceof VisualComponent) {
            //Checks if there is a texture assigned.
            if(textureFile != null) {
                if (!textureFile.equals("")) {
                    componentsBag.add(new NetworkVisualComponent(textureFile, ((VisualComponent) component).width, ((VisualComponent) component).height));
                    return;
                }
            }

            componentsBag.add(new NetworkVisualComponent((VisualComponent) component));
        } else {
            componentsBag.add(component);
        }
    }

    /**
     * Adds components to entity, will convert networkcomponents back to normal components.
     * @param factory The factory the entity has, this will be used when creating a body. Since the networkbodycomponent
     *                doesn't have any shapes this factory will get the shapes according to the type you want to make.
     *                For example, if you want to make a player you have a PlayerFactory, this factory will create a body
     *                with 'playershapes'.
     * @param component The component which needs to be added or converted.
     * @param entity The entity which will be used.
     * @param engine The engine (is needed for creating the body).
     */
    private void addComponentToEntity(Component component, Entity entity) {
        if(component instanceof NetworkBodyComponent) {
            //entity.add(((NetworkBodyComponent) component).getComponent(factory, entity, engine));
        } else if(component instanceof NetworkVisualComponent) {
            entity.edit().add(((NetworkVisualComponent) component).getComponent());
        } else {
            entity.edit().add(component);
        }
    }

    /**
     * This adds all the components in the entity
     * @return The entity created
     */
    public Entity createEntity(World world) {
        Entity entity = world.createEntity();
        for (Component component : componentsBag) {
            addComponentToEntity(component, entity);
        }
        //entity.add(factory.createVisual());
        return entity;
    }
}
