package com.ives.relative.systems.server;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.network.packets.updates.ComponentPacket;
import com.ives.relative.utils.ComponentUtils;

/**
 * Created by Ives on 7/1/2015.
 */
@Wire
public class NetworkSendSystem extends VoidEntitySystem {
    protected NetworkManager networkManager;

    protected ComponentMapper<NetworkC> mNetworkC;

    //TODO see viability for system
    @Override
    protected void processSystem() {

    }

    public ComponentPacket generateFullComponentPacket(Entity e) {
        NetworkManager.Type type = mNetworkC.get(e).type;
        return generateFullComponentPacket(e, type);
    }

    /**
     * This generates a full packet of the desired entity, this packet can be sent to the client or server and the client/
     * server will add this entity to it.
     *
     * @param e
     * @param type
     * @return
     */
    public ComponentPacket generateFullComponentPacket(Entity e, NetworkManager.Type type) {
        Array<Component> components = ComponentUtils.getComponents(e);
        int id = networkManager.getNetworkID(e);
        return new ComponentPacket(components, id, false, -1, type);
    }
}
