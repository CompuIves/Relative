package com.ives.relative.core.packets.handshake;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.ives.relative.assets.modules.Module;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.notice.FinishFileNotice;
import com.ives.relative.core.packets.handshake.notice.StartFileNotice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Ives on 9/12/2014.
 * Compares modules and sends the needed modules by the client.
 * <p/>
 * HANDLED FROM SERVER
 */
public class RequestModules implements Packet {
    List<Module> modules;
    int connectionID;
    int modulePosition;

    transient ModuleInputStreamSender sender;

    public RequestModules() {
    }

    public RequestModules(List<Module> modules, int connectionID) {
        this.modules = modules;
        this.connectionID = connectionID;
    }

    @Override
    public void response(final GameManager game) {
        final List<Module> deltaModules = game.moduleManager.compareModuleLists(modules);
        final Server server = (Server) game.proxy.network.endPoint;
        Connection connection = null;
        for (Connection connection1 : server.getConnections()) {
            if (connection1.getID() == connectionID) {
                connection = connection1;
            }
        }
        if (connection != null) {
            if (modulePosition < deltaModules.size()) {
                try {
                    setupTransfer(connection, game.moduleManager.moduleToBytes(deltaModules.get(modulePosition)));

                    connection.addListener(new Listener() {
                        @Override
                        public void received(Connection connection, Object object) {
                            if (object instanceof CompleteFileNotice) {
                                connection.removeListener(sender);
                                if (modulePosition < deltaModules.size()) {
                                    try {
                                        startTransfer(connection, game.moduleManager.moduleToBytes(deltaModules.get(modulePosition)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    connection.sendTCP(new FinishFileNotice(game.moduleManager.getModules()));
                                    connection.removeListener(sender);
                                    connection.removeListener(this);
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupTransfer(Connection connection, byte[] bytes) {
        connection.sendTCP(new SetupFileTransferPacket());
        connection.sendTCP(new StartFileNotice(bytes.length));

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        connection.addListener(sender = new ModuleInputStreamSender(in, 512, bytes.length, connection));
        modulePosition++;
    }

    private void startTransfer(Connection connection, byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        connection.addListener(sender = new ModuleInputStreamSender(in, 512, bytes.length, connection));
    }
}

class ModuleInputStreamSender extends InputStreamSender {
    int length;
    transient Connection connection;

    public ModuleInputStreamSender(InputStream input, int chunkSize, int length, Connection connection) {
        super(input, chunkSize);
        this.length = length;
        this.connection = connection;
    }

    @Override
    protected Object next(byte[] chunk) {
        return chunk;
    }

    @Override
    protected void start() {
        super.start();
        connection.sendTCP(new StartFileNotice(length));
    }
}
