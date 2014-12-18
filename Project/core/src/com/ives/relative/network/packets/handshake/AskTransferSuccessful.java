package com.ives.relative.network.packets.handshake;

import com.ives.relative.core.GameManager;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 18/12/2014.
 * <p/>
 * HANDLED BY CLIENT
 */
public class AskTransferSuccessful extends ResponsePacket {
    public AskTransferSuccessful() {
    }

    @Override
    public void response(GameManager game) {
        game.network.sendObjectTCP(connection, new ConnectionSuccessful());
    }
}
