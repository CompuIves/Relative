package com.ives.relative.utils;

/**
 * Created by Ives on 12/1/2015.
 */
public class RelativeMath {

    // This method is a *lot* faster than using (int)Math.floor(x)
    public static int fastfloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
