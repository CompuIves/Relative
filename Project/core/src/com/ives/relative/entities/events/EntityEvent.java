package com.ives.relative.entities.events;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Ives on 5/1/2015.
 */
public abstract class EntityEvent implements Pool.Poolable {
    public Entity entity;

    public EntityEvent() {
    }

    public void preReset() {
        entity = null;
        reset();
    }

    /**
     * Is just getClass() but has been added to bypass a glitch in Intellij IDEA
     *
     * @return
     */
    public Class<?> type() {
        return super.getClass();
    }
}
