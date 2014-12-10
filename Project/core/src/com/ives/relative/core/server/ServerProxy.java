package com.ives.relative.core.server;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.entities.components.WorldComponent;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {
    public static GameManager game;

    public ServerProxy(GameManager game) {
        ServerProxy.game = game;
        game.moduleManager.loadModules();
        game.moduleManager.zipAllModules();

        Server server = new Server();
        network = new ServerNetwork(game, server, this);
    }

    public void serverAccepted() {
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
        Entity planet = GameManager.planetFactory.createPlanet("earth", "Earth", new Vector2(0, -10), 8, 3);
        game.engine.addEntity(planet);
    }

    private void generateTerrain() {
        ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
        for(Entity entity : entities) {
            game.terrainGenerator.generateTerrain(entity);
        }
    }
}
