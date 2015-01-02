package com.ives.relative.entities.components;

import com.artemis.Component;
import com.ives.relative.managers.StateManager;

/**
 * Created by Ives on 2/1/2015.
 * States for an entity, states are defined in the {@link com.ives.relative.managers.StateManager}.
 */
public class State extends Component {
    public StateManager.EntityState entityState;

    public State() {
        entityState = StateManager.EntityState.STANDING;
    }
}
