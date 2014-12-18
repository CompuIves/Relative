package com.ives.relative.network.packets.updates;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 18/12/2014.
 */
public class ComponentPacket extends ResponsePacket {
    Component[] components;
    boolean delta;
    long id;
    NetworkManager.Type type;

    public ComponentPacket() {
    }

    public ComponentPacket(Component[] components, long id, boolean delta) {
        this.components = components;
        this.id = id;
        this.delta = delta;
    }

    public ComponentPacket(Component[] components, long id, boolean delta, NetworkManager.Type type) {
        this.components = components;
        this.id = id;
        this.delta = delta;
        this.type = type;
    }


    @Override
    public void response(GameManager game) {
        Array<Component> componentArray = new Array<Component>();
        componentArray.addAll(components);
        if (type != null)
            game.world.getManager(NetworkManager.class).setEntity(id, componentArray, delta, type);
        else
            game.world.getManager(NetworkManager.class).setEntity(id, componentArray, delta);
    }
}
