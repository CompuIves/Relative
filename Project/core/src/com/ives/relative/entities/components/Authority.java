package com.ives.relative.entities.components;

import com.artemis.PooledComponent;
import com.ives.relative.managers.AuthorityManager;

/**
 * Created by Ives on 7/1/2015.
 */
public class Authority extends PooledComponent {
    public int owner = -1;
    public AuthorityManager.AuthorityType type;

    public Authority() {
    }

    public Authority(int owner, AuthorityManager.AuthorityType type) {
        this.owner = owner;
        this.type = type;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public AuthorityManager.AuthorityType getType() {
        return type;
    }

    public void setType(AuthorityManager.AuthorityType type) {
        this.type = type;
    }

    @Override
    protected void reset() {
        owner = -1;
        type = null;
    }
}
