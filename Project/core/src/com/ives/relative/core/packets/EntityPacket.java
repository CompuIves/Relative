package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Component;

import java.util.List;

/**
 * Created by Ives on 4/12/2014.
 */
public class EntityPacket {
    public String identifier;
    public List<Component> components;

    public EntityPacket() {}

    public EntityPacket(String identifier, List<Component> components) {
        this.identifier = identifier;
        this.components = components;
    }
}
