package com.ives.relative.core.server;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.EntityPacket;
import com.ives.relative.core.packets.PacketFactory;
import com.ives.relative.entities.components.WorldComponent;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerNetwork extends Network {
    GameManager game;
    Server server;
    Kryo kryo;

    public ServerNetwork(GameManager game) {
        this.game = game;

        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        kryo = server.getKryo();
        game.registerKryoClasses(kryo);

        server.addListener(this);
    }

    private void startServer() throws IOException{
        this.server = new Server();
        server.start();
        server.bind(54555, 54777);
    }

    public void sendEntityToAll(EntityPacket e) {
        System.out.println("Sent entity with id " + e.message);
        server.sendToAllTCP(e);
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);
    }

    @Override
    public void connected(Connection connection) {
        ImmutableArray<Entity> entityList = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
        for(Entity entity : entityList) {
            sendEntityToAll(PacketFactory.createEntityPacket(entity));
        }
    }
}
