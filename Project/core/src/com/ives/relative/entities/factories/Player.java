package com.ives.relative.entities.factories;


import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Wire;
import com.ives.relative.entities.components.Health;
import com.ives.relative.entities.components.MovementSpeed;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.body.Position;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public interface Player extends EntityFactory<Player> {
    @Bind(Name.class)
    Player name(String internalName, String publicName);

    @Bind(Health.class)
    Player health(int health);

    @Bind(MovementSpeed.class)
    Player mvSpeed(float movementSpeed);

    @Bind(Position.class)
    Player position(float x, float y, int z, String worldID);
}
