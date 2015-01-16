package com.ives.relative.managers.planet.chunks;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 16/1/2015.
 * <p/>
 * This is a side of a planet, sides contain chunks which are rotated to the rotation of the side.
 * A planet contains sides.
 */
public class PlanetSide {
    public transient Map<Vector2, Chunk> chunks;
    public float gravityX, gravityY;
    public int width, height;
    public int rotation;
    String planet;

    public PlanetSide(String planet, int width, int height, int rotation, float gravityX, float gravityY) {
        this.chunks = new HashMap<Vector2, Chunk>(width * height / 2);
        this.width = width;
        this.height = height;
        this.planet = planet;
        this.rotation = rotation;
        this.gravityX = MathUtils.round((float) (gravityX * Math.cos(rotation * MathUtils.degreesToRadians) - gravityY * Math.sin(rotation * MathUtils.degreesToRadians)));
        this.gravityY = MathUtils.round((float) (gravityX * Math.sin(rotation * MathUtils.degreesToRadians) + gravityY * Math.cos(rotation * MathUtils.degreesToRadians)));

        System.out.println(this.gravityX + ", " + this.gravityY);
    }

    /**
     * Generates a chunkmap for this side, a chunk map is an predefined allocation of chunks. These chunks are not loaded.
     */
    public void generateChunkMap() {
        //Richtingscoefficient van chunkbreedte
        int chunkRC = width / height;
        float maxDistX = width - 1;
        float maxDistY = height - 1;
        for (int y = 0; y <= (height + 1) / 2 + 3; y++) {
            for (int x = y * -chunkRC; x <= y * chunkRC; x++) {
                int newX = MathUtils.round((float) (x * Math.cos(rotation * MathUtils.degreesToRadians) - y * Math.sin(rotation * MathUtils.degreesToRadians)));
                int newY = MathUtils.round((float) (x * Math.sin(rotation * MathUtils.degreesToRadians) + y * Math.cos(rotation * MathUtils.degreesToRadians)));

                float gravityReductionMultiplierX = newX / maxDistX * -(float) Math.sin(rotation * MathUtils.degreesToRadians);
                float gravityReductionMultiplierY = newY / maxDistY * (float) Math.cos(rotation * MathUtils.degreesToRadians);
                Chunk chunk = new Chunk(newX, newY, gravityX * gravityReductionMultiplierX, gravityY * gravityReductionMultiplierY, rotation);
                if (y <= (height + 1) / 2) {
                    chunk.planet = planet;
                }
                chunks.put(new Vector2(newX, newY), chunk);
            }
        }
    }
}
