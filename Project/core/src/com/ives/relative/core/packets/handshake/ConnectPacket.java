package com.ives.relative.core.packets.handshake;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.Relative;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Network;
import com.ives.relative.core.packets.*;
import com.ives.relative.entities.components.BodyComponent;
import com.ives.relative.entities.components.HealthComponent;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.mappers.Mappers;
import com.ives.relative.entities.systems.WorldSystem;
import com.ives.relative.planet.tiles.tilesorts.SolidTile;

import java.util.Map;

/**
 * Created by Ives on 8/12/2014.
 * This packet will be sent by the client when it connects to the server
 * It contains the version of the game and the player id, if the version mismatches disconnect, if the player is already
 * in game, disconnect.
 *
 * Handled from: SERVER
 */
public class ConnectPacket implements Packet {
    String version;
    String playerID;
    int connection;

    public ConnectPacket() {
    }

    public ConnectPacket(String version, String playerID, int connection) {
        this.version = version;
        this.playerID = playerID;
        this.connection = connection;
    }

    @Override
    public void handle(GameManager game) {
        System.out.println("Got a connectpacket, checking for version and player...");

        if(Relative.VERSION.equals(version)) {
            //Check for players on the server
            for(Entity entity : game.engine.getEntitiesFor(Family.all(NameComponent.class).get())) {
                String name = Mappers.name.get(entity).internalName;
                if (name.equals(playerID)) {
                    System.out.println("Kicking client, player already connected.");
                    connectionDenied(game.proxy.network, "Player already in the server");
                    break;
                }
            }

            //ACCEPTED SEND PLAYER
            this.connectionAccepted(game.proxy.network, game);

        } else {
            System.out.println("Kicking client, version mismatch.");
            connectionDenied(game.proxy.network, "Version mismatch (local: " + version + " remote: " + Relative.VERSION + ").");
        }
    }

    public void connectionDenied(Network network, final String message) {
        network.sendObjectTCP(connection, new DisconnectPacket("DISCONNECTING: " + message, connection));
    }


    public void connectionAccepted(final Network network, final GameManager game) {
        System.out.println("Connection accepted!");
        System.out.println("Sending acceptedPacket with Player with ID: " + playerID);

        /*
        ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(BodyComponent.class, HealthComponent.class).get());
        for(Entity entity : entities)
            network.sendObjectTCP(connection, new PlayerPacket(8, entity));
        */

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity player = GameManager.playerFactory.createPlayer(playerID, "Player", game.engine.getSystem(WorldSystem.class).getPlanet("earth"),
                        new Vector2(10, 10), 0);
                game.engine.addEntity(player);
                network.sendObjectToAllTCP(new PlayerPacket(connection, player));

            }
        });

        //TODO Look at how this works, is it synchronous, stable, does it create crashes?
        System.out.println("Also sending modules and the world");
        for(Map.Entry entry : game.tileManager.solidTiles.entrySet()) {
            SolidTile tile = (SolidTile) entry.getValue();
            network.sendObjectTCP(connection, new TilePacket(tile));
        }
        network.sendObjectTCP(connection, new CreatePlanetPacket("earth"));
    }
}
