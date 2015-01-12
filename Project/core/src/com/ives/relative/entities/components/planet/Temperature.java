package com.ives.relative.entities.components.planet;

/**
 * Created by Ives on 11/1/2015.
 * <p/>
 * Temperature is used for generation, you give a base temperature and a deviation above and a deviation
 * down.
 * For example: baseTemperature = 20, deviationUpwards = 30, deviationDownwards = 50. Now there is a chance that there
 * will be a temperature of -30 to 50.
 */
public class Temperature {
    public float baseTemperature;
    public float deviationUpwards;
    public float deviationDownwards;

    public Temperature() {
    }
}
