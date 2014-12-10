package com.ives.relative.core.server;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.entities.components.planet.WorldComponent;

import java.util.HashMap;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {
    public static GameManager game;
    public static HashMap<Integer, Entity> players;

    public ServerProxy(GameManager game) {
        ServerProxy.game = game;
        Server server = new Server();
        network = new ServerNetwork(game, server, this);
    }

    public static void addPlayer(int connection, Entity entity) {
        players.put(connection, entity);
    }

    public void serverAccepted() {
        game.moduleManager.loadModules();
        game.moduleManager.zipAllModules();
        players = new HashMap<Integer, Entity>();
        createPlanet();
        generateTerrain();
        registerSystems();
    }

    public void registerSystems() {

    }

    public void update(float delta) {
        //System.out.println("Server is ticking!");
    }

    /**
     * Temporary until custom planets are added
     */
    private void createPlanet() {
        //Already make a planet entity, this will be removed when there are custom planets
        Entity planet = GameManager.planetFactory.createPlanet("earth", "Earth", new Vector2(0, -10), 8, 3, "ivesiscool");
        game.engine.addEntity(planet);
    }

    private void generateTerrain() {
        ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
        for(Entity entity : entities) {
            game.terrainGenerator.generateTerrain(entity);
        }
    }

}
