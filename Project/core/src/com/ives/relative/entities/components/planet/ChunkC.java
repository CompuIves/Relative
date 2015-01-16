package com.ives.relative.entities.components.planet;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.ives.relative.managers.planet.Chunk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 4/1/2015.
 */
public class ChunkC extends Component {
    public transient Map<Vector2, Chunk> chunks;
    public int chunkSize = 64;

    public ChunkC() {
        chunks = new HashMap<Vector2, Chunk>();
    }

    public ChunkC(int chunkSize) {
        this();
        this.chunkSize = chunkSize;
    }
}
