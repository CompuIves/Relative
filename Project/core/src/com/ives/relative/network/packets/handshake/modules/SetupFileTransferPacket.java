package com.ives.relative.network.packets.handshake.modules;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.network.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.network.packets.handshake.modules.notice.StartFileNotice;
import com.ives.relative.network.packets.handshake.planet.RequestWorld;

import java.io.IOException;

/**
 * Created by Ives on 9/12/2014.
 * <p/>
 * The foundation of the file transfer, this packet sets up a listener on the client for incoming module zips. When it
 * receives a {@link com.ives.relative.network.packets.handshake.modules.notice.CompleteFileNotice} it will create a zip
 * file of all the bytes it has received. When it receives a {@link com.ives.relative.network.packets.handshake.modules.notice.StartFileNotice}
 * (containing the file size of the file coming and the name + version) it will prepare for a new file. When it receives
 * a {@link com.ives.relative.network.packets.handshake.modules.notice.FinishFileTransferNotice} (containing the list of
 * modules used by the server) it will unzip all module zips and activate the list of modules from the FinishFileTransferNotice.
 * <p>The order of notice packets is for example this: StartFile -> CompleteFile -> StartFile -> CompleteFile -> FinishFileTransfer</p>
 * <p></p>
 * HANDLED BY CLIENT
 */
public class SetupFileTransferPacket extends ResponsePacket {
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

                    //Ask for the rest of the world.
                    game.network.sendObjectTCP(connection.getID(), new RequestWorld());
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
