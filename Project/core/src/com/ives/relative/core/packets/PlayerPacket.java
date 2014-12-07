package com.ives.relative.core.packets;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.entities.components.InputComponent;
import com.ives.relative.entities.factories.PlayerFactory;
import com.ives.relative.entities.systems.WorldSystem;

/**
 * Created by Ives on 7/12/2014.
 * Server sends a packet to the client with a player, this gets called when the client connects and when a new client connects
 * connectionID for knowing which client sent it (to take control over the player)
 */
public class PlayerPacket implements Packet {
    int connectionID;
    String internalName;
    String realName;
    String worldID;
    float x, y;
    int z;

    public PlayerPacket() {
    }

    public PlayerPacket(String internalName, String realName, String worldID, float x, float y, int z, int connectionID) {
        this.internalName = internalName;
        this.realName = realName;
        this.worldID = worldID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.connectionID = connectionID;
    }

    @Override
    public void handle(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Entity entity = PlayerFactory.createPlayer(internalName, realName, game.engine.getSystem(WorldSystem.class).getPlanet(worldID), new Vector2(x, y), z);
                //Add a inputcomponent for handling inputs to the player!
                if(game.proxy.network.connectionID == connectionID)
                    entity.add(new InputComponent());
                game.engine.addEntity(entity);
            }
        });
    }
}
