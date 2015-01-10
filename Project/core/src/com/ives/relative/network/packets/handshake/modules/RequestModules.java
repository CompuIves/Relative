package com.ives.relative.network.packets.handshake.modules;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.server.ServerNetwork;
import com.ives.relative.managers.assets.modules.Module;
import com.ives.relative.managers.assets.modules.ModuleManager;
import com.ives.relative.network.Network;
import com.ives.relative.network.packets.ResponsePacket;
import com.ives.relative.network.packets.handshake.modules.notice.CompleteFileNotice;
import com.ives.relative.network.packets.handshake.modules.notice.FinishFileTransferNotice;
import com.ives.relative.network.packets.handshake.modules.notice.StartFileNotice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Ives on 9/12/2014.
 * Compares modules and sends the needed modules by the client.
 * <p/>
 * It is set up like this:
 * The server sends a setup packet to the client, the packet will register a listener at the client for new files,
 * after this the server will send a packet named starttransfer which says how big the file is and what the name
 * of the module is. Then the server will send the module in chunks of 512bytes. The client will say when it has
 * received all the bytes and the server will execute an new starttransfer until all packets have been sent.
 * <p/>
 * <p/>
 * HANDLED FROM SERVER
 */
public class RequestModules extends ResponsePacket {
    List<Module> modules;

    transient List<Module> deltaModules;

    transient ModuleInputStreamSender sender;

    public RequestModules() {
    }

    public RequestModules(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    public void response(final GameManager game) {
        deltaModules = game.world.getManager(ModuleManager.class).compareModuleLists(modules);
        Connection connection = ServerNetwork.getConnection(this.connection);
        if (connection != null) {
            //Set up a transfer in all cases, otherwise the planet can't be requested (will be sent next packet)
            setupTransfer(connection, game.network);

            connection.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof CompleteFileNotice) {
                        if (sender != null)
                            connection.removeListener(sender);
                        if (deltaModules.size() > 0) {
                            startTransferModule(connection);
                        } else {
                            connection.sendTCP(new FinishFileTransferNotice(game.world.getManager(ModuleManager.class).getModules()));
                            connection.removeListener(this);
                        }
                    }
                }
            });
        }
    }

    private void setupTransfer(Connection connection, Network network) {
        network.sendObjectTCP(connection.getID(), new SetupFileTransferPacket());

        //If there never was a module to be sent it will be completed instantly
        if (deltaModules.size() == 0) {
            connection.sendTCP(new CompleteFileNotice());
        } else {
            startTransferModule(connection);
        }
    }

    private void startTransferModule(Connection connection) {
        if (deltaModules.size() > 0) {
            Module module = deltaModules.get(0);
            try {
                byte[] bytes = ModuleManager.moduleToBytes(module);
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                connection.sendTCP(new StartFileNotice(bytes.length, module.name, module.version));
                connection.addListener(sender = new ModuleInputStreamSender(in, 512, bytes.length));
                deltaModules.remove(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ModuleInputStreamSender extends InputStreamSender {
    int length;

    public ModuleInputStreamSender(InputStream input, int chunkSize, int length) {
        super(input, chunkSize);
        this.length = length;
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
