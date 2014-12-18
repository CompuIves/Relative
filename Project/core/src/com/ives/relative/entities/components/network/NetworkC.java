package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 13/12/2014.
 */
public class NetworkC extends Component implements Networkable {
    public int id;
    public NetworkManager.Type type;

    public NetworkC() {
    }

    public NetworkC(int id, NetworkManager.Type type) {
        this.id = id;
        this.type = type;
    }
}
