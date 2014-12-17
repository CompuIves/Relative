package com.ives.relative.entities.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;

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
        for (Component component : components) {
            e.edit().add(component);
            System.out.println("Added: " + component.getClass().getSimpleName());
        }

        return e;
    }
}
