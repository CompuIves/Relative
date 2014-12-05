package com.ives.relative.core;

import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.packets.EntityPacket;

/**
 * Created by Ives on 4/12/2014.
 */
public abstract class Network extends Listener {
    public void sendEntityPacketTCP(EntityPacket e) {}
}
