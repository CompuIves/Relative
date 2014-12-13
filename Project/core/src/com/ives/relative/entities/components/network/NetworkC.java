package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.ives.relative.core.network.networkentity.NetworkEntity;

/**
 * Created by Ives on 13/12/2014.
 */
public class NetworkC extends Component {
    public long id;
    public NetworkEntity.Type type;

    public NetworkC() {
    }

    public NetworkC(long id, NetworkEntity.Type type) {
        this.id = id;
        this.type = type;
    }
}
