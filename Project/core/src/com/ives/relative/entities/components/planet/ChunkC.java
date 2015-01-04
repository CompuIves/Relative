package com.ives.relative.entities.components.planet;

import com.artemis.Component;
import com.ives.relative.managers.planet.Chunk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ives on 4/1/2015.
 */
public class ChunkC extends Component {
    public transient Map<Integer, Chunk> chunks;

    public ChunkC() {
        chunks = new HashMap<Integer, Chunk>();
    }
}
