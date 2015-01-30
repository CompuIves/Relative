package com.ives.relative.managers.server;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.PlayerManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.State;
import com.ives.relative.entities.components.body.Physics;
import com.ives.relative.entities.components.body.Position;
import com.ives.relative.entities.components.body.Transform;
import com.ives.relative.entities.components.body.Velocity;
import com.ives.relative.entities.components.client.Visual;
import com.ives.relative.entities.components.living.Health;
import com.ives.relative.entities.components.living.MovementSpeed;
import com.ives.relative.entities.components.network.NetworkC;
import com.ives.relative.entities.events.creation.CreatePlayerEvent;
import com.ives.relative.factories.PlayerFactory;
import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.managers.event.EventManager;
import com.ives.relative.universe.UniverseBody;
import com.ives.relative.universe.chunks.ChunkManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 11/12/2014.
 * This is the player manager of the server, only the server uses this.
 */
@Wire
public class ServerPlayerManager extends PlayerManager {
    protected NetworkManager networkManager;
    protected AuthorityManager authorityManager;
    protected ChunkManager chunkManager;

    //For the server
    private Map<Integer, Entity> playersByConnection;
    private Map<Entity, Integer> connectionsByPlayers;

    private Map<Integer, String> playersLoggingIn;

    public ServerPlayerManager() {
        super();
        playersByConnection = new HashMap<Integer, Entity>();
        connectionsByPlayers = new HashMap<Entity, Integer>();
        playersLoggingIn = new HashMap<Integer, String>();
    }

    /**
     * Creates a player
     *
     * @param connection   the connection the player connects with (every player has a unique connection), purely used for finding the player by connection.
     * @param internalName local name
     * @param realName     name visible to other players
     * @param position     position
     * @param z            depth
     * @return a new entity (player)
     */
    public Entity createPlayer(int connection, String internalName, String realName, UniverseBody universeBody, Vector2 position, int z) {
        Entity e = new EntityBuilder(world).with(new Health(100),
                new MovementSpeed(5f),
                new Name(internalName, realName),
                new Visual(new TextureRegion(new Texture("player.png")), 0.9f, 1.8f),
                new Position(position.x, position.y, z, 0, universeBody),
                new Velocity(0, 0, 0),
                new State()).
                group("players").
                build();

        Body body = PlayerFactory.createBody(universeBody, e, position.x, position.y, 0, 0);
        e.edit().add(new Physics(body, BodyDef.BodyType.DynamicBody)).add(new Transform(1, 1, null));

        setPlayer(e, internalName);

        authorityManager.authorizeEntity(connection, e, AuthorityManager.AuthorityType.PERMANENT);
        world.getManager(EventManager.class).notifyEvent(new CreatePlayerEvent(e));

        int id = networkManager.addEntity(e);
        e.edit().add(new NetworkC(id, 0, NetworkManager.Type.PLAYER));

        chunkManager.addEntityToChunk(e);
        return e;
    }

    /**
     * Save a player by connection, used only by Server
     *
     * @param connection connection id
     * @param player     player
     */
    public void addConnection(int connection, Entity player) {
        this.playersByConnection.put(connection, player);
        this.connectionsByPlayers.put(player, connection);
    }

    public void removeConnection(int connection) {
        Entity player = this.playersByConnection.get(connection);
        this.playersByConnection.remove(connection);
        this.connectionsByPlayers.remove(player);
    }

    /**
     * Get a player by connection, used only by Server
     *
     * @param connection connection id
     * @return player
     */
    public Entity getPlayerByConnection(int connection) {
        return this.playersByConnection.get(connection);
    }

    public int getConnectionByPlayer(Entity e) {
        if (connectionsByPlayers.containsKey(e)) {
            return connectionsByPlayers.get(e);
        } else {
            return -1;
        }
    }

    public Array<Entity> getPlayers() {
        Array<Entity> entities = new Array<Entity>();
        for (Map.Entry entry : playersByConnection.entrySet()) {
            entities.add((Entity) entry.getValue());
        }
        return entities;
    }

    public boolean isPlayer(Entity e) {
        return connectionsByPlayers.containsKey(e);
    }

    /**
     * Adds the player to a temporary list of clients that are logging in, this way the client
     * will not receive entity updates while still loading.
     *
     * @param connection connectionID
     * @param id         playerName
     */
    public void addPlayerLoggingIn(int connection, String id) {
        playersLoggingIn.put(connection, id);
    }

    /**
     * Returns the player which was trying to log in so the server can create the player.
     * The playermanager will remove the player from the tempList.
     *
     * @param connection ConnectionID
     * @return playername
     */
    public String finishPlayerLoggingIn(int connection) {
        String playerName = playersLoggingIn.get(connection);
        playersLoggingIn.remove(connection);
        return playerName;
    }

    public Array<Integer> getConnections() {
        Array<Integer> connections = new Array<Integer>(connectionsByPlayers.size());
        for (Map.Entry entry : connectionsByPlayers.entrySet()) {
            connections.add((Integer) entry.getValue());
        }
        return connections;
    }
}
