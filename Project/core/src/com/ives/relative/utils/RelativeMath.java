package com.ives.relative.utils;

/**
 * Created by Ives on 12/1/2015.
 */
public class RelativeMath {

    // This method is a *lot* faster than using (int)Math.floor(x)
    public static int fastfloor(double a) {
        int xi = (int) a;
        return a < xi ? xi - 1 : xi;
    }

    public static boolean isInBounds(int i, int startI, int endI) {
        return i >= startI && i <= endI;
    }
}
