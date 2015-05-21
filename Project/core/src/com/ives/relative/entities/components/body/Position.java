package com.ives.relative.entities.components.body;

import com.artemis.Entity;
import com.artemis.World;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.UniverseManager;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 12/12/2014.
 * Position component, this component gets synced with the body component
 */
public class Position extends CustomNetworkComponent {
    public float x, y;
    public float px, py;

    public int z;
    public int pz;

    /**
     * rotation in radians
     */
    public float rotation;
    public float protation;

    public transient Space space;

    public String universeBodyID;
    public transient Chunk chunk;

    public Position() {
    }

    public Position(float x, float y, int z, float rotation, Space space) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.space = space;
        this.rotation = rotation;
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {
        universeBodyID = space.id;
    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        space = world.getSystem(UniverseManager.class).getSpace(universeBodyID);
    }
}
