package com.ives.relative.core.packets.handshake;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.notice.FinishFileNotice;
import com.ives.relative.core.packets.handshake.notice.StartFileNotice;

/**
 * Created by Ives on 9/12/2014.
 */
public class SetupFileTransferPacket implements Packet {
    int position;
    int length;
    byte[] bytes;

    public SetupFileTransferPacket() {
    }

    @Override
    public void response(final GameManager game) {
        position = 0;

        game.proxy.network.endPoint.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof StartFileNotice) {
                    startFileTransfer((StartFileNotice) object);
                } else if (object instanceof byte[]) {
                    processByte((byte[]) object);
                } else if (object instanceof FinishFileNotice) {
                    game.moduleManager.indexModules();
                    game.moduleManager.loadModules(((FinishFileNotice) object).moduleList);
                    game.proxy.network.endPoint.removeListener(this);
                }

                if (position == length) {
                    System.out.println("Transfer of file has been succeeded");
                    position = 0;
                    connection.sendTCP(new CompleteFileNotice());
                }
            }
        });
    }

    private void startFileTransfer(StartFileNotice object) {
        position = 0;
        bytes = new byte[length];
        length = object.length;
    }

    private void processByte(byte[] object) {
        for (byte newByte : object) {
            bytes[position] = newByte;
            position++;
        }
        System.out.println("Received a " + object.length + " byte");
    }

    private void saveBytes() {

    }
}
