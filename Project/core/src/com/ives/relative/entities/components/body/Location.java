package com.ives.relative.entities.components.body;

import com.artemis.Entity;
import com.artemis.World;
import com.ives.relative.entities.components.network.CustomNetworkComponent;
import com.ives.relative.managers.NetworkManager;
import com.ives.relative.universe.Space;
import com.ives.relative.universe.UniverseSystem;
import com.ives.relative.universe.chunks.Chunk;

/**
 * Created by Ives on 12/12/2014.
 * Location component, the x, y and z components are stored in the body of the entity.
 */
public class Location extends CustomNetworkComponent {
    public transient Space space;

    public String universeBodyID;
    public transient Chunk chunk;

    public Location() {
    }

    public Location(Space space) {
        this.space = space;
    }

    @Override
    public void convertForSending(Entity e, World world, NetworkManager.Type type) {
        universeBodyID = space.id;
    }

    @Override
    public void convertForReceiving(Entity e, World world, NetworkManager.Type type) {
        space = world.getSystem(UniverseSystem.class).getSpace(universeBodyID);
    }
}
