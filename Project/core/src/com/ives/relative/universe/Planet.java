package com.ives.relative.universe;

import com.badlogic.gdx.math.Vector2;
import com.ives.relative.universe.chunks.builders.ChunkBuilder;

/**
 * Created by Ives on 18/1/2015.
 */
public class Planet extends UniverseBody {
    private final String name;
    private final String seed;

    private Vector2 gravity;
    private float mass;

    private Planet(Builder builder) {
        super(builder.planetarySystem, builder.x, builder.y, builder.width, builder.height);
        this.name = builder.name;
        this.seed = builder.seed;

        this.gravity = builder.gravity;
        setChunkBuilder(builder.chunkBuilder);
    }

    /**
     * Builder pattern, read it in Effective Java. This is used so the planet can be created dynamically
     */
    public static class Builder {
        private final String name;
        private final String seed;

        private final PlanetarySystem planetarySystem;
        private final int x, y;
        private final int width, height;

        private final ChunkBuilder chunkBuilder;

        private Vector2 gravity;
        private float mass;

        public Builder(String name, String seed, PlanetarySystem planetarySystem, int x, int y, int width, int height, ChunkBuilder chunkBuilder) {
            this.name = name;
            this.seed = seed;
            this.planetarySystem = planetarySystem;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.chunkBuilder = chunkBuilder;
        }

        public Builder setMass(float mass) {
            this.mass = mass;
            return this;
        }

        public Builder setGravity(Vector2 gravity) {
            this.gravity = gravity;
            return this;
        }

        public Planet build() {
            return new Planet(this);
        }
    }
}
