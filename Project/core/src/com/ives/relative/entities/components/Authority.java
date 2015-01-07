package com.ives.relative.entities.components;

import com.artemis.PooledComponent;
import com.ives.relative.managers.AuthorityManager;

/**
 * Created by Ives on 7/1/2015.
 */
public class Authority extends PooledComponent {
    public int owner;
    public AuthorityManager.AuthorityType type;

    public Authority() {
    }

    public Authority(int owner, AuthorityManager.AuthorityType type) {
        this.owner = owner;
        this.type = type;
    }

    @Override
    protected void reset() {
        owner = -1;
        type = null;
    }
}
