package com.ives.relative.entities.components.mappers;

import com.badlogic.ashley.core.ComponentMapper;
import com.ives.relative.entities.components.*;
import com.ives.relative.entities.components.planet.GravityComponent;
import com.ives.relative.entities.components.planet.SeedComponent;
import com.ives.relative.entities.components.planet.WorldComponent;

/**
 * Created by Ives on 3/12/2014.
 */
public class Mappers {
    public static final ComponentMapper<TileComponent> tile = ComponentMapper.getFor(TileComponent.class);
    public static final ComponentMapper<VisualComponent> visual = ComponentMapper.getFor(VisualComponent.class);
    public static final ComponentMapper<BodyComponent> body = ComponentMapper.getFor(BodyComponent.class);
    public static final ComponentMapper<WorldComponent> world = ComponentMapper.getFor(WorldComponent.class);
    public static final ComponentMapper<MovementSpeedComponent> mvSpeed = ComponentMapper.getFor(MovementSpeedComponent.class);
    public static final ComponentMapper<InputComponent> input = ComponentMapper.getFor(InputComponent.class);
    public static final ComponentMapper<NameComponent> name = ComponentMapper.getFor(NameComponent.class);
    public static final ComponentMapper<GravityComponent> gravity = ComponentMapper.getFor(GravityComponent.class);
    public static final ComponentMapper<SeedComponent> seed = ComponentMapper.getFor(SeedComponent.class);
}
