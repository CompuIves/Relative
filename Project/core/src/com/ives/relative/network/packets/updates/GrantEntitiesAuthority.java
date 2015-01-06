package com.ives.relative.network.packets.updates;

import com.badlogic.gdx.utils.Array;
import com.ives.relative.network.packets.BasePacket;

/**
 * Created by Ives on 6/1/2015.
 */
public class GrantEntitiesAuthority extends BasePacket {
    public int[] entities;

    public GrantEntitiesAuthority() {
    }

    public GrantEntitiesAuthority(int sequence, Array<Integer> entities) {
        super(sequence);
        this.entities = new int[entities.size];
        for (int i = 0; i < entities.size; i++) {
            this.entities[i] = entities.get(i);
        }
    }
}
