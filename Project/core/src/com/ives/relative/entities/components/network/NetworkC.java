package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 13/12/2014.
 * This makes an entity networkable, it can be sent over the network to other clients or to the server.
 */
public class NetworkC extends Component {
    public int id;
    public NetworkManager.Type type;

    public int priority;
    public int acc;

    public NetworkC() {
    }

    /**
     * @param id       The ID of the entity
     * @param priority Priority of entity, lower is more priority. Players have priority 0 for examples while tiles have
     *                 priority 1. This changes the update frequency of the entity over the network.
     * @param type     The type of the entity
     */
    public NetworkC(int id, int priority, NetworkManager.Type type) {
        this.id = id;
        this.priority = priority;
        this.type = type;
    }
}
