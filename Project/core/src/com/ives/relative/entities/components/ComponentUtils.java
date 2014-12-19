package com.ives.relative.entities.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.utils.Bag;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.living.MovementSpeed;

/**
 * Created by Ives on 17/12/2014.
 * These are utilities for components.
 */
public class ComponentUtils {

    public static Array<Component> getComponents(Entity e) {
        Array<Component> componentsArray = new Array<Component>();
        Bag<Component> components = new Bag<Component>();
        e.getComponents(components);

        for (Component component : components) {
            componentsArray.add(component);
        }

        System.out.println("Components: ");
        for (Component component : components) {
            System.out.println(component.getClass().getSimpleName());
        }

        return componentsArray;
    }

    public static Entity addComponents(Entity e, Array<Component> components) {
        Entity entity = e.getWorld().createEntity();
        entity.edit().add(new MovementSpeed());

        entity.deleteFromWorld();

        EntityEdit entityEdit = e.edit();
        for (Component component : components) {
            entityEdit.add(component);
            //entityEdit.create(component.getClass());
            System.out.println("Added: " + component.getClass().getSimpleName());
        }
        return e;
    }

    public static void transferComponents(Entity from, Entity to) {
        Bag<Component> components = new Bag<Component>();
        from.getComponents(components);

        for (Component component : components) {
            to.edit().add(component);
        }
    }

    public static Entity removeAllComponents(Entity e) {
        if (e != null) {
            EntityEdit edit = e.edit();
            Bag<Component> components = new Bag<Component>();

            Physics physics = e.getComponent(Physics.class);
            if (physics != null) {
                removePhysics(physics);
            }

            e.getComponents(components);
            for (Component c : components) {
                edit.remove(c);
            }
        }
        return e;
    }

    private static void removePhysics(Physics physics) {
        Body body = physics.body;
        if (body != null)
            physics.body.getWorld().destroyBody(body);
    }
}
