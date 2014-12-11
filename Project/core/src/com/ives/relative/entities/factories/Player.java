package com.ives.relative.entities.factories;


import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Wire;
import com.ives.relative.entities.components.HealthComponent;
import com.ives.relative.entities.components.MovementSpeedComponent;
import com.ives.relative.entities.components.NameComponent;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public interface Player extends EntityFactory<Player> {
    @Bind(NameComponent.class)
    Player name(String internalName, String publicName);

    @Bind(HealthComponent.class)
    Player health(int health);

    @Bind(MovementSpeedComponent.class)
    Player mvSpeed(float movementSpeed);
}
