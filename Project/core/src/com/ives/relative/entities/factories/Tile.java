package com.ives.relative.entities.factories;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Wire;
import com.ives.relative.entities.components.Durability;

/**
 * Created by Ives on 5/12/2014.
 */
@Wire
public interface Tile extends EntityFactory<Tile> {
    @Bind(Durability.class)
    Tile durability(int durability);
}
