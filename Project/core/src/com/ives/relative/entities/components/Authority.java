package com.ives.relative.entities.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.utils.Array;
import com.ives.relative.managers.AuthorityManager;

/**
 * Created by Ives on 7/1/2015.
 */
public class Authority extends PooledComponent {
    public Array<Integer> owners;
    public AuthorityManager.AuthorityType type;

    public Authority() {
        owners = new Array<Integer>();
        owners.ordered = true;
    }

    public Array<Integer> getOwners() {
        return owners;
    }

    public void setOwners(Array<Integer> owners) {
        this.owners = owners;
    }

    public int getOwner() {
        if (owners.size > 0) {
            return owners.get(0);
        } else {
            return -1;
        }
    }

    public void setOwner(int owner) {
        if (type != AuthorityManager.AuthorityType.PERMANENT) {
            if (!owners.contains(owner, true)) {
                owners.add(owner);
            }
        } else {
            if (owners.size == 0) {
                owners.add(owner);
            }
        }
    }

    public void removeOwner(int owner) {
        owners.removeValue(owner, true);
    }

    public AuthorityManager.AuthorityType getType() {
        return type;
    }

    public void setType(AuthorityManager.AuthorityType type) {
        this.type = type;
    }

    @Override
    protected void reset() {
        owners.clear();
        type = null;
    }
}
