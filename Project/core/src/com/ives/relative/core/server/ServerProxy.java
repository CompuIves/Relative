package com.ives.relative.core.server;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.Server;
import com.ives.relative.assets.modules.ModuleIndexer;
import com.ives.relative.core.GameManager;
import com.ives.relative.core.Proxy;
import com.ives.relative.entities.components.WorldComponent;

/**
 * Created by Ives on 4/12/2014.
 */
public class ServerProxy extends Proxy {
    public static GameManager game;
    ModuleIndexer moduleIndexer;

    public ServerProxy(GameManager game) {
        this.game = game;
        moduleIndexer = new ModuleIndexer(game.tileManager);
        moduleIndexer.indexModules(false);
        moduleIndexer.loadModules();
        generateTerrain();
        registerSystems();

        Server server = new Server();
        network = new ServerNetwork(game, server);
    }

    public void registerSystems() {

    }

    public void update(float delta) {
        //System.out.println("Server is ticking!");
    }

    private void generateTerrain() {
        ImmutableArray<Entity> entities = game.engine.getEntitiesFor(Family.all(WorldComponent.class).get());
        for(Entity entity : entities) {
            game.terrainGenerator.generateTerrain(entity);
        }
    }
}
