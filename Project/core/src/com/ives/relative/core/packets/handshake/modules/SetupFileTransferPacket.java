package com.ives.relative.core.packets.handshake.modules;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.core.packets.handshake.modules.notice.StartFileNotice;
import com.ives.relative.core.packets.handshake.planet.RequestPlanetPacket;
import com.ives.relative.managers.assets.modules.ModuleManager;

import java.io.IOException;

/**
 * Created by Ives on 9/12/2014.
 *
 * The foundation of the file transfer
 * HANDLED BY CLIENT
 */
public class SetupFileTransferPacket implements Packet {
    int position;
    int length;
    byte[] bytes;

    String moduleName;
    String moduleVersion;

    public SetupFileTransferPacket() {
    }

    @Override
    public void response(final GameManager game) {
        position = 0;

        game.network.endPoint.addListener(new Listener() {
            @Override
            public void received(final Connection connection, final Object object) {
                if (object instanceof StartFileNotice) {
                    startFileTransfer((StartFileNotice) object);
                } else if (object instanceof byte[]) {
                    processByte((byte[]) object);
                } else if (object instanceof FinishFileTransferNotice) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            game.world.getManager(ModuleManager.class).indexModules();
                            game.world.getManager(ModuleManager.class).loadModules(((FinishFileTransferNotice) object).moduleList);
                            //Ask for the world now that the modules are loaded.
                        }
                    });

                    //Ask for the planet
                    connection.sendTCP(new RequestPlanetPacket(connection.getID()));
                    game.network.endPoint.removeListener(this);
                }

                if (position == length) {
                    System.out.println("Transfer of file has been succeeded");
                    System.out.println("Total bytes transferred: " + length);
                    position = 0;
                    try {
                        if (bytes != null)
                            ModuleManager.bytesToModule(bytes, moduleName, moduleVersion);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    connection.sendTCP(new CompleteFileNotice());
                }
            }
        });
    }

    private void startFileTransfer(StartFileNotice object) {
        System.out.println("Starting transfer of " + object.moduleName);
        position = 0;
        length = object.length;
        bytes = new byte[length];
        moduleName = object.moduleName;
        moduleVersion = object.moduleVersion;
    }

    private void processByte(byte[] object) {
        for (byte newByte : object) {
            bytes[position] = newByte;
            position++;
        }
    }
}
