package com.ives.relative.entities.factories;

import com.artemis.EntityFactory;
import com.artemis.annotations.Bind;
import com.artemis.annotations.Wire;
import com.ives.relative.entities.components.NameComponent;
import com.ives.relative.entities.components.planet.SeedComponent;

/**
 * Created by Ives on 12/1/2014.
 */
@Wire
public interface Planet extends EntityFactory<Planet> {
    @Bind(NameComponent.class)
    Planet name(String internalName, String publicName);

    @Bind(SeedComponent.class)
    Planet seed(String seed);
}
