package com.ives.relative.entities.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.utils.Bag;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;

/**
 * Created by Ives on 17/12/2014.
 * These are utilities for components.
 */
public class ComponentUtils {

    /**
     * Gets all the components from a entity
     *
     * @param e
     * @return
     */
    public static Array<Component> getComponents(Entity e) {
        Array<Component> componentsArray = new Array<Component>();
        Bag<Component> components = new Bag<Component>();
        e.getComponents(components);

        for (Component component : components) {
            componentsArray.add(component);
        }
        return componentsArray;
    }

    /**
     * Adds the given components to the entity
     * @param e
     * @param components
     * @return
     */
    public static Entity addComponents(Entity e, Array<Component> components) {
        EntityEdit entityEdit = e.edit();
        for (Component component : components) {
            entityEdit.add(component);
        }
        return e;
    }

    /**
     * Copies components from the first entity to the second one.
     * @param from
     * @param to
     */
    public static void transferComponents(Entity from, Entity to) {
        Bag<Component> components = new Bag<Component>();
        from.getComponents(components);

        EntityEdit edit = to.edit();
        for (Component component : components) {
            edit.add(component);
        }
    }

    /**
     * Removes all components from the entity
     * @param e
     * @return
     */
    public static Entity removeAllComponents(Entity e) {
        if (e != null) {
            Bag<Component> components = new Bag<Component>();

            Physics physics = e.getComponent(Physics.class);
            if (physics != null) {
                removePhysics(physics);
            }

            EntityEdit edit = e.edit();
            e.getComponents(components);
            for (Component c : components) {
                edit.remove(c.getClass());
            }
        }
        return e;
    }

    /**
     * Removes the body from the world.
     * @param physics
     */
    private static void removePhysics(Physics physics) {
        Body body = physics.body;
        if (body != null)
            physics.body.getWorld().destroyBody(body);
    }
}
