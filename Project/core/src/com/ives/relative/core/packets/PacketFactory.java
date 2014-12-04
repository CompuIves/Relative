package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 4/12/2014.
 */
public class PacketFactory {
    public static EntityPacket createEntityPacket(Entity e) {
        ArrayList<Component> components = new ArrayList<Component>();
        for(Component component : e.getComponents()) {
            components.add(component);
        }
        EntityPacket entityPacket = new EntityPacket();
        entityPacket.message = e.getId() + "";
        return entityPacket;
    }
}
