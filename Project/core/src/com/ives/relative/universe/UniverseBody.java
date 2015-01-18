package com.ives.relative.universe;

import com.badlogic.gdx.utils.Array;
import com.ives.relative.universe.chunks.builders.ChunkBuilder;
import com.ives.relative.universe.chunks.builders.EmptyChunk;
import com.ives.relative.utils.RelativeMath;

import java.util.Iterator;

/**
 * Created by Ives on 18/1/2015.
 * <p/>
 * All objects having this contain a position in the universe. It has a width and a height and coordinates (which are
 * aligned to the middle).
 */
public class UniverseBody {
    protected final int x, y;
    protected final int width, height;

    protected final UniverseBody parent;
    protected final Array<UniverseBody> children;

    public ChunkBuilder chunkBuilder;

    public UniverseBody(UniverseBody parent, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.parent = parent;
        this.children = new Array<UniverseBody>();

        this.chunkBuilder = new EmptyChunk(this);
    }

    public void setChunkBuilder(ChunkBuilder chunkBuilder) {
        this.chunkBuilder = chunkBuilder;
    }

    /**
     * Gets the lowest child in the UniverseBody hierarchy, lowest possibility is planet.
     *
     * @param x
     * @param y
     * @return
     */
    public UniverseBody getChild(int x, int y) {
        Iterator<UniverseBody> it = children.iterator();
        UniverseBody lowestUniverseBody = null;

        while (it.hasNext()) {
            UniverseBody universeBody = it.next();
            if (universeBody.isInBody(x, y)) {
                //starts the loop from the beginning with the new UniverseBody
                lowestUniverseBody = universeBody;
                it = universeBody.children.iterator();
            }
        }

        return lowestUniverseBody;
    }

    public boolean isInBody(int x, int y) {
        return RelativeMath.isInBounds(x, this.x - width / 2, this.x + width / 2)
                && RelativeMath.isInBounds(y, this.y - height / 2, this.y + height / 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniverseBody that = (UniverseBody) o;

        if (height != that.height) return false;
        if (width != that.width) return false;
        if (x != that.x) return false;
        if (y != that.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "UniverseBody at x: " + x + ", y: " + y + ", with width: " + width + ", height: " + height;
    }
}
