package com.ives.relative.core.server;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.EntityPacket;
import com.ives.relative.core.packets.PacketFactory;
import com.ives.relative.core.packets.PlayerPacket;
import com.ives.relative.core.packets.components.BodyNetworkComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.WorldComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.factories.PlayerFactory;

import javax.lang.model.element.Name;
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

    @Override
    public void sendObjectTCP(Object o) {
        System.out.println("Sent an object!");
        server.sendToAllTCP(o);
    }

    @Override
    public void received(Connection connection, final Object object) {
        if(object instanceof EntityPacket) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    EntityPacket playerPacket = (EntityPacket) object;
                    if(playerPacket.identifier.equals("player")) {
                        BodyNetworkComponent bodyComponent;
                        NameComponent nameComponent = null;
                        String worldname = "";
                        for(Component component : playerPacket.components) {
                            if(component instanceof BodyNetworkComponent) {
                                bodyComponent = (BodyNetworkComponent) component;
                                worldname = bodyComponent.world;
                            } else if(component instanceof NameComponent) {
                                nameComponent = (NameComponent) component;
                            }
                        }
                        //TODO fix this shit!
                        World world = null;
                        for (Entity entity : game.engine.getEntitiesFor(Family.all(WorldComponent.class, NameComponent.class).get())) {
                            String name = Mappers.name.get(entity).internalName;
                            if (name.equals(worldname)) {
                                world = Mappers.world.get(entity).world;
                            }
                        }

                        Entity e = PlayerFactory.createPlayer(nameComponent.internalName, nameComponent.publicName, world, new Vector2(playerPacket.x, playerPacket.y), playerPacket.z);
                        game.engine.addEntity(e);

                        System.out.println("Received and created player: " + playerPacket.internalName + "in world" +
                                world);
                    }
                }
            });

        }
    }

    @Override
    public void connected(Connection connection) {
        ImmutableArray<Entity> entityList = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
        for(Entity entity : entityList) {
            sendObjectTCP(PacketFactory.createFullEntityPacket(entity, "requestPlayer"));
        }
    }
}
