package com.ives.relative.universe;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Ives on 18/1/2015.
 */
public class Planet extends UniverseBody {
    public final String seed;

    public final Vector2 gravity;
    private float mass;

    private Planet(Builder builder) {
        super(builder.name, builder.planetarySystem, builder.x, builder.y, builder.width, builder.height, builder.rotation, builder.scale);
        this.seed = builder.seed;

        this.gravity = builder.gravity;
    }

    @Override
    protected void update() {
        super.update();
        this.rotation += 10 * (1 / 60f);
        setTransform();
        updateBody();
    }

    /**
     * Builder pattern, read it in Effective Java. This is used so the planet can be created dynamically
     */
    public static class Builder {
        private final String name;
        private final String seed;

        private final UniverseBody planetarySystem;
        private final int x, y;
        private final int width, height;
        private float rotation;
        private Vector2 scale;

        private Vector2 gravity;
        private float mass;

        public Builder(String name, String seed, UniverseBody planetarySystem, int x, int y, int width, int height, Vector2 gravity) {
            this.name = name;
            this.seed = seed;
            this.planetarySystem = planetarySystem;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.gravity = gravity;

            scale = new Vector2(1, 1);
        }

        public Builder setMass(float mass) {
            this.mass = mass;
            return this;
        }

        /**
         * Set rotation
         *
         * @param rotation rotation in degrees
         * @return this builder for chaining.
         */
        public Builder setRotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setScale(Vector2 scale) {
            this.scale = scale;
            return this;
        }

        public Planet build() {
            return new Planet(this);
        }
    }
}
