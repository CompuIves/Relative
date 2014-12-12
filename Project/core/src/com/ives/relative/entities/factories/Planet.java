package com.ives.relative.entities.factories;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Wire;
import com.ives.relative.entities.components.Name;
import com.ives.relative.entities.components.planet.Seed;

/**
 * Created by Ives on 12/1/2014.
 */
@Wire
public interface Planet extends EntityFactory<Planet> {
    @Bind(Name.class)
    Planet name(String internalName, String publicName);

    @Bind(Seed.class)
    Planet seed(String seed);
}
