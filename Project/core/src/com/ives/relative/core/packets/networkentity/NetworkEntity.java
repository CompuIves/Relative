package com.ives.relative.core.packets.networkentity;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.VisualComponent;
import com.ives.relative.entities.components.network.NetworkBodyComponent;
import com.ives.relative.entities.components.network.NetworkVisualComponent;
import com.ives.relative.entities.factories.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 8/12/2014.
 * This entity is a stripped down version of a normal entity, instead of having Signals and that sort of stuff this
 * entity only has components.
 * It also converts incompatible components to networkcomponents and back.
 */
public class NetworkEntity {
    public List<Component> componentList;

    public NetworkEntity() {
    }

    public NetworkEntity(Entity entity) {
        componentList = new ArrayList<Component>();

        for(Component component : entity.getComponents()) {
            addComponent(component);
        }
    }

    private void addComponent(Component component) {
        if(component instanceof BodyComponent) {
            componentList.add(new NetworkBodyComponent((BodyComponent) component));
        } else if(component instanceof VisualComponent) {
            componentList.add(new NetworkVisualComponent((VisualComponent) component));
        } else {
            componentList.add(component);
        }
    }

    private void addComponentToEntity(Factory factory, Component component, Entity entity, Engine engine) {
        if(component instanceof NetworkBodyComponent) {
            entity.add(((NetworkBodyComponent) component).getComponent(factory, entity, engine));
        } else if(component instanceof NetworkVisualComponent) {
            entity.add(((NetworkVisualComponent) component).getComponent());
        } else {
            entity.add(component);
        }
    }

    /**
     * This adds all the components in the entity, the bodycomponent too, this will have to be read after the
     * @return
     */
    public Entity createEntity(Factory factory, Engine engine) {
        Entity entity = new Entity();
        for(Component component : componentList) {
            addComponentToEntity(factory, component, entity, engine);
        }
        //entity.add(factory.createVisual());
        return entity;
    }
}
