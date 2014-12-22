package com.ives.relative.systems.client;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.MovementSpeed;
import com.ives.relative.entities.components.client.InputC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.updates.ComponentPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Ives on 22/12/2014.
 */
@Wire
public class NetworkReceiveSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;
    Map<Integer, Array<Component>> changedEntities;
    BlockingQueue<ComponentPacket> queue;

    public NetworkReceiveSystem() {
        changedEntities = new HashMap<Integer, Array<Component>>();
        queue = new LinkedBlockingQueue<ComponentPacket>();
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected void begin() {
        try {
            for (int i = 0; i < queue.size(); i++) {
                ComponentPacket packet = queue.take();
                int id = packet.entityID;
                NetworkManager.Type type = packet.type;
                boolean delta = packet.delta;

                Array<Component> componentArray = new Array<Component>();
                componentArray.addAll(packet.components);

                boolean works = false;
                for (Component component : componentArray) {
                    if (component.getClass().isAssignableFrom(MovementSpeed.class)) {
                        System.out.println("WORKS");
                        works = true;
                    }
                }

                if (!works) {
                    if (componentArray.size > 6) {
                        Class<?> klass = componentArray.get(7).getClass();
                        System.out.println("DIDN'T WORK");
                    }
                } else {
                    if (componentArray.size > 6) {
                        Class<?> klass = componentArray.get(7).getClass();
                        System.out.println("COOL");
                    }
                }

                Entity e;
                if (type != null) {
                    e = networkManager.addEntity(id, componentArray, delta, type);
                } else {
                    e = networkManager.addEntity(id, componentArray, delta);
                }

                int playerID = world.getSystem(ClientNetworkSystem.class).getPlayerID();
                if (id == playerID) {
                    e.edit().add(new InputC());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDataForProcessing(ComponentPacket componentPacket) {
        queue.add(componentPacket);
    }
}
