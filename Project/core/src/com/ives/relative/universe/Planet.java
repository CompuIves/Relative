package com.ives.relative.universe;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.managers.CollisionManager;

/**
 * Created by Ives on 18/1/2015.
 */
public class Planet {
    public final String seed;
    public final String name;

    public final Vector2 gravity;
    public final SolarSystem solarSystem;
    private Space space;
    private float mass;
    private final int x, y;
    private final int width, height;

    private Planet(Builder builder) {
        this.seed = builder.seed;

        this.x = builder.x;
        this.y = builder.y;
        this.width = builder.width;
        this.height = builder.height;

        this.name = builder.name;
        this.solarSystem = builder.solarSystem;
        builder.solarSystem.planets.add(this);

        this.gravity = builder.gravity;
    }


    public Space getSpace() {
        if(space == null)
            space = new Space(name, null, width, height, 16, gravity);

        return space;
    }

    /**
     * Builder pattern, read it in Effective Java. This is used so the planet can be created dynamically
     */
    public static class Builder {
        private final String name;
        private final String seed;

        private final SolarSystem solarSystem;
        private final int x, y;
        private final int width, height;

        private Vector2 gravity;
        private float mass;

        public Builder(String name, String seed, SolarSystem solarSystem, int x, int y, int width, int height, Vector2 gravity) {
            this.name = name;
            this.seed = seed;
            this.solarSystem = solarSystem;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.gravity = gravity;
        }

        public Builder setMass(float mass) {
            this.mass = mass;
            return this;
        }
        public Planet build() {
            return new Planet(this);
        }
    }
}
