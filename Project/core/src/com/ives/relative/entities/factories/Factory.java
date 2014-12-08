package com.ives.relative.entities.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.ives.relative.entities.components.VisualComponent;

/**
 * Created by Ives on 8/12/2014.
 */
public abstract class Factory {

    public Body createBody(Entity e, float x, float y, float vx, float vy, Entity planet) {
        return null;
    }

    public VisualComponent createVisual() {
        return null;
    }
}
