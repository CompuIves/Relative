package com.ives.relative.network.packets.updates;

import com.ives.relative.managers.AuthorityManager;
import com.ives.relative.network.packets.BasePacket;

/**
 * Created by Ives on 6/1/2015.
 */
public class GrantEntityAuthority extends BasePacket {
    public int entity;
    public AuthorityManager.AuthorityType type;

    public GrantEntityAuthority() {
    }

    public GrantEntityAuthority(int sequence, int entity, AuthorityManager.AuthorityType type) {
        super(sequence);
        this.entity = entity;
        this.type = type;
    }
}
