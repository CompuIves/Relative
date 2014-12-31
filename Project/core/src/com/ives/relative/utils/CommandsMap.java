package com.ives.relative.utils;

import java.util.HashMap;

/**
 * Created by Ives on 5/12/2014.
 * This map is just an extensions of an existing map. The only difference is that this map returns a default object if
 * it doesn't contain the key asked.
 */
public class CommandsMap<K, V> extends HashMap<K, V> {
    private V defaultCommand;

    public CommandsMap(V defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    @Override
    public V get(Object key) {
        if (key == null || !containsKey(key)) {
            return defaultCommand;
        } else {
            return super.get(key);
        }
    }
}
