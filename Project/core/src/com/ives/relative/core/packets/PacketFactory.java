package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.TileComponent;
import com.ives.relative.entities.components.WorldComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ives on 4/12/2014.
 */
public class PacketFactory {

    public static EntityPacket createFullEntityPacket(Entity e, String identifier) {
        List<Component> components = new ArrayList<Component>();
        List<String> extraInfo = new ArrayList<String>();

        //This filters object components so only the true info components will be added in the packet
        for(Component component : e.getComponents()) {
            if(!(component instanceof WorldComponent || component instanceof BodyComponent || component instanceof TileComponent || component instanceof InputComponent)) {
                components.add(component);
            }
        }
        EntityPacket entityPacket = new EntityPacket(identifier, components);
        return entityPacket;
    }

}
