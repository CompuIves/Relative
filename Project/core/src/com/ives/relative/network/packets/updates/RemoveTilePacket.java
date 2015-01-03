package com.ives.relative.network.packets.updates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.core.GameManager;
import com.ives.relative.managers.TileSystem;
import com.ives.relative.network.packets.ResponsePacket;

/**
 * Created by Ives on 3/1/2015.
 */
public class RemoveTilePacket extends ResponsePacket {
    float x, y;

    public RemoveTilePacket() {
        super();
    }

    public RemoveTilePacket(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void response(final GameManager game) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.world.getSystem(TileSystem.class).removeTile(new Vector2(x, y));
            }
        });
    }
}
