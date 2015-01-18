package com.ives.relative.universe;

import com.artemis.Manager;
import com.artemis.annotations.Wire;

/**
 * Created by Ives on 18/1/2015.
 */
@Wire
public class UniverseManager extends Manager {
    public final Universe universe;

    public UniverseManager() {
        universe = new Universe("ivesiscool");
    }
}
