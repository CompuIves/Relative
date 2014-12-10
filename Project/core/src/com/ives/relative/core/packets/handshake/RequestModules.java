package com.ives.relative.core.packets.handshake;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.ives.relative.assets.modules.Module;
import com.ives.relative.assets.modules.ModuleManager;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.packets.Packet;
import com.ives.relative.core.packets.handshake.notice.CompleteFileNotice;
import com.ives.relative.core.packets.handshake.notice.FinishFileTransferNotice;
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

    transient List<Module> deltaModules;

    transient ModuleInputStreamSender sender;

    public RequestModules() {
    }

    public RequestModules(List<Module> modules, int connectionID) {
        this.modules = modules;
        this.connectionID = connectionID;
    }

    @Override
    public void response(final GameManager game) {
        deltaModules = game.moduleManager.compareModuleLists(modules);
        final Server server = (Server) game.proxy.network.endPoint;
        Connection connection = null;
        for (Connection connection1 : server.getConnections()) {
            if (connection1.getID() == connectionID) {
                connection = connection1;
            }
        }
        if (connection != null) {
            if (deltaModules.size() > 0) {
                setupTransfer(connection);

                connection.addListener(new Listener() {
                    @Override
                    public void received(Connection connection, Object object) {
                        if (object instanceof CompleteFileNotice) {
                            connection.removeListener(sender);
                            if (deltaModules.size() > 0) {
                                startTransferModule(connection);
                            } else {
                                connection.sendTCP(new FinishFileTransferNotice(game.moduleManager.getModules()));
                                connection.removeListener(sender);
                                connection.removeListener(this);
                            }
                        }
                    }
                });
            }
        }
    }

    private void setupTransfer(Connection connection) {
        connection.sendTCP(new SetupFileTransferPacket());
        startTransferModule(connection);
    }

    private void startTransferModule(Connection connection) {
        Module module = deltaModules.get(0);
        try {
            byte[] bytes = ModuleManager.moduleToBytes(module);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            connection.sendTCP(new StartFileNotice(bytes.length, module.name, module.version));
            connection.addListener(sender = new ModuleInputStreamSender(in, 512, bytes.length, connection, module));
            deltaModules.remove(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ModuleInputStreamSender extends InputStreamSender {
    int length;
    transient Connection connection;
    transient Module module;

    public ModuleInputStreamSender(InputStream input, int chunkSize, int length, Connection connection, Module module) {
        super(input, chunkSize);
        this.length = length;
        this.connection = connection;
        this.module = module;
    }

    @Override
    protected Object next(byte[] chunk) {
        return chunk;
    }

    @Override
    protected void start() {
        super.start();
    }
}
