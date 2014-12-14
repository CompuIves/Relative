package com.ives.relative.entities.commands;

import java.util.HashMap;

/**
 * Created by Ives on 5/12/2014.
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
