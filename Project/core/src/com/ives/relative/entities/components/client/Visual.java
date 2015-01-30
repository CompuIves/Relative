package com.ives.relative.entities.components.client;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.entities.components.tile.TileC;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 3/12/2014.
 * Gives the entity the ability to visual represent itself.
 */
public class Visual extends CustomNetworkComponent {
    public transient TextureRegion texture;
    public float width;
    public float height;

    public Visual() {
    }

    public Visual(TextureRegion texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {
        if (type == NetworkManager.Type.TILE) {
            dependants.add(TileC.class.getSimpleName());
        }
    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        switch (type) {
            case PLAYER:
                texture = new TextureRegion(new Texture("player.png"));
                break;
            case TILE:
                TileC tileC = world.getMapper(TileC.class).get(e);
                texture = tileC.tile.textureRegion;

                dependants.clear();
                break;
        }
    }
}
