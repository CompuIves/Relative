package com.ives.relative.entities.components.network;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.NetworkManager;

/**
 * Created by Ives on 30/1/2015.
 * <p/>
 * Components which have non-primitive fields cannot be sent easily over the network. These components have to be
 * converted when sending and converted back when receiving. That's what this class is for.
 */
public abstract class CustomNetworkComponent extends Component {

    /**
     * Some components have to be converted before others, this field will specify on which components it relies *
     */
    public Array<String> dependants;

    public CustomNetworkComponent() {
        this.dependants = new Array<String>();
    }

    /**
     * Converts the component to a component which can be sent
     *
     * @param e     entity
     * @param world the world, maybe needed for accessing managers/systems
     * @param type  type of the entity if it has one, maybe needed for specific transformations
     */
    public abstract void convertForSending(Entity e, World world, NetworkManager.Type type);

    /**
     * Converts the component to a component which can be sent
     *
     * @param e     entity
     * @param world the world, maybe needed for accessing managers/systems
     * @param type  type of the entity if it has one, maybe needed for specific transformations
     */
    public abstract void convertForReceiving(Entity e, World world, NetworkManager.Type type);
}
