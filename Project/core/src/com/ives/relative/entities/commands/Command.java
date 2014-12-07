package com.ives.relative.entities.commands;

import com.badlogic.ashley.core.Entity;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.entities.components.mappers.Mappers;

/**
 * Created by Ives on 5/12/2014.
 */
public class Command implements Packet {
    String entityID;
    public void execute(Entity entity) {this.entityID = Mappers.name.get(entity).internalName;}

    /**
     * Do the opposite of execute! For example when the key is released.
     * @param entity the entity which should be acted upon
     */
    public void antiExecute(Entity entity) {}

    @Override
    public void handle(GameManager game) {

    }
}
