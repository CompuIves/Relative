package com.ives.relative.network.packets.updates;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.systems.network.ClientNetworkSystem;

/**
 * Created by Ives on 18/12/2014.
 */
public class ComponentPacket extends ResponsePacket {
    Component[] components;
    boolean delta;
    int id;
    NetworkManager.Type type;

    public ComponentPacket() {
    }

    public ComponentPacket(Array<Component> components, int id, boolean delta) {
        this.components = new Component[components.size];
        for (int i = 0; i < components.size; i++) {
            this.components[i] = components.get(i);
        }
        this.id = id;
        this.delta = delta;
    }

    public ComponentPacket(Array<Component> components, int id, boolean delta, NetworkManager.Type type) {
        this.components = new Component[components.size];
        for (int i = 0; i < components.size; i++) {
            this.components[i] = components.get(i);
        }
        this.id = id;
        this.delta = delta;
        this.type = type;
    }


    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Array<Component> componentArray = new Array<Component>();
                componentArray.addAll(components);

                Entity e;
                if (type != null) {
                    e = game.world.getManager(NetworkManager.class).addEntity(id, componentArray, delta, type);
                } else {
                    e = game.world.getManager(NetworkManager.class).addEntity(id, componentArray, delta);
                }

                int playerID = game.world.getSystem(ClientNetworkSystem.class).getPlayerID();
                if (id == playerID) {
                    e.edit().add(new InputC());
                }
            }
        });
    }
}
