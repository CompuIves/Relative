package com.ives.relative.core.client;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.EntityPacket;
import com.ives.relative.core.packets.PacketFactory;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.factories.PlayerFactory;

import java.io.IOException;

/**
 * Created by Ives on 4/12/2014.
 */
public class ClientNetwork extends Network {
    GameManager game;
    Client client;
    Kryo kryo;

    public ClientNetwork(GameManager game) {
        this.game = game;

        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        kryo = client.getKryo();
        game.registerKryoClasses(kryo);

        client.addListener(this);
    }

    private void startClient() throws IOException {
        client = new Client();
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        if(object instanceof EntityPacket) {
            if(((EntityPacket) object).identifier.equals("requestPlayer"))
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    //This is just temporary player info
                    String internalName = "player1";
                    String publicName = "Ives";
                    String worldName = "earth";
                    float x = 10.0f;
                    float y = 10.0f;
                    int z = 0;

                    //TODO fix this shit!
                    World world = null;
                    for(Entity entity : game.engine.getEntitiesFor(Family.all(WorldComponent.class, NameComponent.class).get())) {
                        String name = Mappers.name.get(entity).internalName;
                        if(name.equals(worldName)) {
                            world = Mappers.world.get(entity).world;
                        }
                    }

                    Entity entity = PlayerFactory.createPlayer(internalName, publicName, world, new Vector2(x, y), z);
                    sendObjectTCP(PacketFactory.createFullEntityPacket(entity, "player"));

                    entity.add(new InputComponent());
                    game.engine.addEntity(entity);
                    System.out.println("Sent and created: " + internalName + "in world" + worldName);
                }
            });

        }
    }

    @Override
    public void sendObjectTCP(Object o) {
        client.sendTCP(o);
    }
}
