package com.ives.relative.core.network;

import java.util.Comparator;

/**
 * Created by Ives on 9/12/2014.
 */
public class KryoComparator implements Comparator<Class<? extends Object>> {

    @Override
    public int compare(Class<? extends Object> o1, Class<? extends Object> o2) {
        return o1.getSimpleName().compareToIgnoreCase(o2.getSimpleName());
    }
}
